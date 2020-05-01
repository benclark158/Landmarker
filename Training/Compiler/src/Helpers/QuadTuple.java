package Helpers;

/**
 * Created by Ben Clark on 07/12/2019.
 */
public class QuadTuple<A, B, C, D> {

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
     * fourth item in the tuple.
     */
    public D d;

    /**
     * Initialise for tuple type.
     *
     * @param a     first variable
     * @param b     second variable
     * @param c     third variable
     * @param d     third variable
     */
    public QuadTuple(A a, B b, C c, D d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
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
        if(this.d != null){
            hash = 3 * hash +  this.d.hashCode();
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
        if (obj instanceof QuadTuple) {
            if (((QuadTuple) obj).a != null && ((QuadTuple) obj).a.equals(this.a)) {
                if (((QuadTuple) obj).b != null && ((QuadTuple) obj).b.equals(this.b)) {
                    if(((QuadTuple) obj).c != null && ((QuadTuple) obj).c.equals(this.c)) {
                        if(((QuadTuple) obj).d != null && ((QuadTuple) obj).d.equals(this.d)) {
                            return obj.hashCode() == this.hashCode();
                        }
                    }
                }
            }
        }
        return false;
    }
}
