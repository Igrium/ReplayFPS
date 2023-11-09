package com.igrium.replayfps.test;

import java.util.concurrent.Executor;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

import com.igrium.replayfps.util.ConcurrentBuffer;

public class ConcurrentBufferTest {
    class DemoBuffer extends ConcurrentBuffer<Integer> {

        public DemoBuffer(Executor executor) {
            super(executor);
            setBufferSize(256);
            setBufferThreshold(1024);
        }

        @Override
        protected Integer load(int index) throws Exception {
            Thread.sleep(1);

            if (index == 69420) {
                throw new Exception("Test Exception");
            }

            if (index > 1024) return null;


            return index;
        }
        
    }
    
    @RepeatedTest(8)
    public void testBuffer() throws Exception {
        SimpleSingleThreadExecutor executor = new SimpleSingleThreadExecutor(r -> new Thread(r, "BufferThread"));
        DemoBuffer buffer = new DemoBuffer(executor);

        int val0 = buffer.poll();
        Assertions.assertEquals(0, val0);

        int val1 = buffer.poll();
        Assertions.assertEquals(1, val1);

        buffer.seek(2048);
        // if (buffer.hasErrored()) {
        //     throw buffer.getError().get();
        // }
        Assertions.assertEquals(null, buffer.poll());
        
        buffer.seek(3);
        Assertions.assertEquals(3, buffer.peek());
        Assertions.assertEquals(3, buffer.poll());

        buffer.seek(69420);
        buffer.peek();
        Assertions.assertNotNull(buffer.getError().orElse(null));

        Assertions.assertNull(buffer.peek());

        executor.shutdown();
    }
}
