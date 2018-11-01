package skunk

import cats.arrow.Profunctor

/**
 * SQL, parameter encoder, and row decoder for a statement that returns rows. We assume that `sql`
 * has the same number of placeholders of the form `$1`, `$2`, etc., as the number of slots encoded
 * by `encoder`, that `sql` selects the same number of columns are the number of slots decoded by
 * `decoder`, and that the parameter and column types specified by `encoder` and `decoder` are
 * consistent with the schema. The `check` methods on [[skunk.Session Session]] provide a means to
 * verify this assumption.
 *
 * You can construct a `Query` directly, although it is more typical to use the `sql`
 * interpolator.
 *
 * {{{
 * sql"SELECT name, age FROM person WHERE age > $int2".query(varchar ~ int2) // Query[Short, String ~ Short]
 * }}}
 *
 * @param sql A SQL statement returning no rows.
 * @param encoder An encoder for all parameters `$1`, `$2`, etc., in `sql`.
 * @param encoder A decoder for selected columns.
 *
 * @see [[skunk.syntax.StringContextOps StringContextOps]] for information on the `sql`
 *   interpolator.
 * @see [[skunk.Session Session]] for information on executing a `Query`.
 *
 * @group Statements
 */
final case class Query[A, B](sql: String, encoder: Encoder[A], decoder: Decoder[B]) {

  /**
   * Query is a profunctor.
   * @group Transformations
   */
  def dimap[C, D](f: C => A)(g: B => D): Query[C, D] =
    Query(sql, encoder.contramap(f), decoder.map(g))

  /**
   * Query is a contravariant functor in `A`.
   * @group Transformations
   */
  def contramap[C](f: C => A): Query[C, B] =
    dimap[C, B](f)(identity)

  /**
   * Query is a covariant functor in `B`.
   * @group Transformations
   */
  def map[D](g: B => D): Query[A, D] =
    dimap[A, D](identity)(g)

}

/** @group Companions */
object Query {

  implicit val ProfunctorQuery: Profunctor[Query] =
    new Profunctor[Query] {
      def dimap[A, B, C, D](fab: Query[A,B])(f: C => A)(g: B => D) =
        fab.dimap(f)(g)
    }

}