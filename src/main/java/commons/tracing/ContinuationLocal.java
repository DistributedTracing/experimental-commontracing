package commons.tracing;

public final class ContinuationLocal<T> {
    private static InheritableThreadLocal<Object[]> localCtx = new InheritableThreadLocal<Object[]>();
    private static volatile int size = 0;

    public static Object[] save() {
        return localCtx.get();
    }

    public static void restore(Object[] saved) {
        localCtx.set(saved);
    }

    private static synchronized int add() {
        size += 1;
        return size - 1;
    }

    private static void set(int i, Object v) {
        assert i < size;
        Object[] ctx = localCtx.get();
        if(ctx == null) {
            ctx = new Object[size];
        } else {
            Object[] oldCtx = ctx;
            ctx = new Object[size];
            System.arraycopy(oldCtx, 0, ctx, 0, oldCtx.length);
        }

        ctx[i] = v;
        localCtx.set(ctx);
    }

    private static Object get(int i) {
        Object[] ctx = localCtx.get();
        if(ctx == null || ctx.length <= i) {
            return null;
        }
        Object v =  ctx[i];
        if(v == null) {
            return null;
        } else {
            return v;
        }
    }

    private static void clear() {
        localCtx.set(null);
    }

    // === More static stuff here, maybe

    private final int me = add();

    public void set(T value) {
        set(me, value);
    }

    public T get() {
        return (T)get(me);
    }

    public T apply() {
        return get();
    }
}
