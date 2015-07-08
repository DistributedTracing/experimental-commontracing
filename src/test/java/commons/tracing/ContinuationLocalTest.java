package commons.tracing;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class ContinuationLocalTest {
    @Test
    public void make_new_continuation_local() {
        ContinuationLocal<String> myVariable = new ContinuationLocal<String>();
    }

    @Test
    public void set_a_value() {
        ContinuationLocal<Integer> integer = new ContinuationLocal<Integer>();
        integer.set(3);
        assertThat(integer.get(), is(3));
        integer.set(5);
        assertThat(integer.get(), is(5));
    }

    @Test
    public void should_be_local_to_a_thread() throws Exception {
        final ContinuationLocal<Integer> myVariable = new ContinuationLocal<Integer>();
        myVariable.set(1000);
        Thread[] threads = new Thread[8];
        final int[] values = new int[threads.length];

        for(int i = 0; i < threads.length; i++) {
            final int y = i;
            threads[y] = new Thread(new Runnable() {
                public void run() {
                    myVariable.set(y);
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    values[y] = myVariable.get();
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        for(int i = 0; i < threads.length; i++) {
            assertThat(values[i], is(i));
            threads[i].join();
        }
        assertThat(myVariable.get(), is(1000));
    }

    @Test
    public void state_should_be_passed_into_a_new_thread() throws InterruptedException {
        final ContinuationLocal<Integer> myVariable = new ContinuationLocal<Integer>();
        myVariable.set(50);
        final AtomicReference<Integer> result = new AtomicReference<Integer>();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                result.set(myVariable.get());
            }
        });
        thread.start();
        thread.join();
        assertThat(result.get(), is(50));
    }

    @Test
    public void should_save_state_and_restore_it() {
        final ContinuationLocal<Integer> myVariable = new ContinuationLocal<Integer>();
        myVariable.set(5);
        Object[] context = ContinuationLocal.save();
        myVariable.set(6);
        ContinuationLocal.restore(context);
        assertThat(myVariable.get(), is(5));
    }
}
