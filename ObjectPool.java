public class ObjectPool implements ObjectPool_IF {
    private Object lockObject;
    private int size; // Number of free objects
    private int instanceCount; // Total number of objects
    private int maxInstances; // Capacity
    private ObjectCreation_IF creator;
    private Object[] pool;
    private static ObjectPool poolInstance;

    private ObjectPool(ObjectCreation_IF c, int max) {
        this.lockObject = new Object();
        this.size = 0;
        this.instanceCount = 0;
        this.maxInstances = max;
        this.pool = new Object[max];
        this.creator = c;
    }

    public static ObjectPool getPoolInstance(ObjectCreation_IF c, int max) {
        if (poolInstance == null) {
            poolInstance = new ObjectPool(c, max);
        }
        return poolInstance;
    }

    public int getSize() {
        synchronized (this.lockObject) {
            return this.size;
        }
    }

    public int getCapacity() {
        return this.maxInstances;
    }

    public void setCapacity(int c) {
        if (c <= 0) {
            String msg = "Capacity must be greater than 0: " + c;
            throw new IllegalArgumentException(msg);
        }

        synchronized (this.lockObject) {
            Object[] newPool = new Object[c];
            System.arraycopy(this.pool, 0, newPool, 0, c);
            this.pool = newPool;
        }
    }

    public Object getObject() {
        synchronized (this.lockObject) {
            Object thisObject = this.removeObject();
            if (thisObject != null) {
                return thisObject;
            } else if (this.instanceCount < this.maxInstances) {
                return this.createObject();
            } else {
                return null;
            }
        }
    }

    public Object waitForObject() throws InterruptedException {
        synchronized (this.lockObject) {
            Object thisObject = removeObject();

            if (thisObject != null) {
                return thisObject;
            }

            if (this.instanceCount < this.maxInstances) {
                return this.createObject();
            }

            while (thisObject == null) {
                this.lockObject.wait();
                thisObject = removeObject();
            }
            return thisObject;
        }
    }

    private Object removeObject() {
        synchronized (this.lockObject) {
            if (this.pool.length > 0) {
                Object lastObject = this.pool[pool.length - 1];
                this.pool[pool.length - 1] = null;
                this.instanceCount--;
                return lastObject;
            }

            return null;
        }
    }

    public void release(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }

        synchronized (this.lockObject) {
            if (this.getSize() < this.getCapacity()) {
                this.pool[this.size] = o;
                this.size++;
                this.lockObject.notify();
            }
        }
    }

    private Object createObject() {
        Object newObject = this.creator.create();
        this.pool[this.pool.length - 1] = newObject;
        this.instanceCount++;
        return newObject;
    }
}
