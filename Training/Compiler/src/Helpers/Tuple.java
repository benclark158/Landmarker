package Helpers;

/**
 * Created by Ben Clark on 07/12/2019.
 */
public class Tuple<A, B> {

    /**
     * First item in the tuple.
     */
    public A a;

    /**
     * Second item in the tuple.
     */
    public B b;

    /**
     * Initialise for tuple type.
     *
     * @param a     first variable
     * @param b     second variable
     */
    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Gets the hashcode of the tuple.
     *
     * @return  returns a unique number for this object
     */
    @Override
    public int hashCode() {
        int hash = 7;
        if (this.a != null) {
            hash = 3 * hash + this.a.hashCode();
        }
        if (this.b != null) {
            hash = 3 * hash + this.b.hashCode();
        }
        return hash;
    }

    /**
     * Checks if this object is equal to another object.
     *
     * @param obj   object to compare to
     * @return      boolean value - true if equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple) {
            if (((Tuple) obj).a != null && ((Tuple) obj).a.equals(this.a)) {
                if (((Tuple) obj).b != null && ((Tuple) obj).b.equals(this.b)) {
                    return obj.hashCode() == this.hashCode();
                }
            }
        }
        return false;
    }
}
