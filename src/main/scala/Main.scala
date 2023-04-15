
/*
 * Run sbt, call `runMain Main` and see non termination.
 * If you call one of the working* method in place of one of the blocking*
 * in TEST object last line, the program ends as expected.
 */
object Main {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    // this call works as expected
    zio.Unsafe.unsafe(implicit unsafe => {
      zio.Runtime.default.unsafe
        .run(zio.ZIO.foreachPar(List("a", "b", "c", "d").take(2))(x => zio.ZIO.attempt(println(x))).withParallelism(4))
        .getOrThrowFiberFailure()
    })

    // the call here will block
    TEST.boot
  }
}

/*
 * Calling from an other object blocks on some cases. The one I discover are:
 * - when we use foreachPar in place of foreach IF the size of the collection is > 1
 * - when we use Console.printLine in place of ZIO.attempt(println())
 */
object TEST {

  def workingExampleForeachParSize1 = {
    zio.Unsafe.unsafe(implicit unsafe => {
      zio.Runtime.default.unsafe
        .run(zio.ZIO.foreachPar(List("a", "b", "c", "d").take(1))(x => zio.ZIO.attempt(println(x))).withParallelism(4))
        .getOrThrowFiberFailure()
    })
  }

  def blockingForeachParSize2 = {
    zio.Unsafe.unsafe(implicit unsafe => {
      zio.Runtime.default.unsafe
        .run(zio.ZIO.foreachPar(List("a", "b", "c", "d").take(2))(x => zio.ZIO.attempt(println(x))).withParallelism(4))
        .getOrThrowFiberFailure()
    })
  }

  def workingExampleForeach = {
    zio.Unsafe.unsafe(implicit unsafe => {
      zio.Runtime.default.unsafe
        .run(zio.ZIO.foreach(List("a", "b", "c", "d"))(x => zio.ZIO.attempt(println(x))).withParallelism(4))
        .getOrThrowFiberFailure()
    })
  }

  def blockingExampleConsole = {
    zio.Unsafe.unsafe(implicit unsafe => {
      zio.Runtime.default.unsafe
        .run(zio.ZIO.foreach(List("a", "b", "c", "d"))(x => zio.Console.printLine(x)).withParallelism(4))
        .getOrThrowFiberFailure()
    })
  }

  val boot = println("test")
  println("******************************** simple foreachPar in TEST  *********************************")
  blockingExampleConsole
}
