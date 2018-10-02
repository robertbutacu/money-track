package cli

object Parser {
  def parse(args: List[String]): Command = {
    val splitArgs = args.map(arg => arg.split('=').toList)

    splitArgs.foreach(r => println(r))

    NoCommand
  }
}
