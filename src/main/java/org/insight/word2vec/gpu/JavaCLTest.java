package org.insight.word2vec.gpu;

import org.bridj.Pointer;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLBuildException;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;

public class JavaCLTest {


	public static void main(String[] args) {
        try {
            Pointer<Float> a = Pointer.pointerToFloats(1,  2,  3,  4 );
            Pointer<Float> b = Pointer.pointerToFloats(10, 20, 30, 40);

            Pointer<Float> sum = add(a, b);
            for (long i = 0, n = sum.getValidElements(); i < n; i++)
                System.out.println(sum.get(i));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}

	public static Pointer<Float> add(Pointer<Float> a, Pointer<Float> b) throws CLBuildException {
		int n = (int)a.getValidElements();

		CLContext context = JavaCL.createBestContext();
		CLQueue queue = context.createDefaultQueue();

		String source = 
			"__kernel void addFloats(__global const float* a, __global const float* b, __global float* output)     " +
			"{                                                                                                     " +
			"   int i = get_global_id(0);                                                                          " +
			"   output[i] = a[i] + b[i];                                                                           " +
			"}                                                                                                     ";

		CLKernel kernel = context.createProgram(source).createKernel("addFloats");
		CLBuffer<Float> aBuf = context.createBuffer(CLMem.Usage.Input, a, true);
		CLBuffer<Float> bBuf = context.createBuffer(CLMem.Usage.Input, b, true);
		CLBuffer<Float> outBuf = context.createBuffer(CLMem.Usage.Output, Float.class, n);
		kernel.setArgs(aBuf, bBuf, outBuf);

		kernel.enqueueNDRange(queue, new int[]{n});
		queue.finish();

		return outBuf.read(queue);
	}

}
