package utilities;

@FunctionalInterface
public interface TriFunction<A, B, C, D> {

	public D apply(A a, B b, C c);

	@FunctionalInterface
	public static interface TriPredicate<A, B, C> {
		public boolean test(A a, B b, C c);
	}
}
