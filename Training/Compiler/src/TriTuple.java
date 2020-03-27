/**
 * Created by Ben Clark on 07/12/2019.
 */
public class TriTuple<A, B, C> {

    /**
     * First item in the tuple.
     */
    public A a;

    /**
     * Second item in the tuple.
     */
    public B b;

    /**
     * third item in the tuple.
     */
    public C c;

    /**
     * Initialise for tuple type.
     *
     * @param a     first variable
     * @param b     second variable
     * @param c     third variable
     */
    public TriTuple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
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
        if (this.c != null){
            hash = 3 * hash + this.c.hashCode();
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
        if (obj instanceof TriTuple) {
            if (((TriTuple) obj).a != null && ((TriTuple) obj).a.equals(this.a)) {
                if (((TriTuple) obj).b != null && ((TriTuple) obj).b.equals(this.b)) {
                    if(((TriTuple) obj).c != null && ((TriTuple) obj).c.equals(this.c)) {
                        return obj.hashCode() == this.hashCode();
                    }
                }
            }
        }
        return false;
    }
}
