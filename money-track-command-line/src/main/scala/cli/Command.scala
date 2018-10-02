package cli

sealed trait Command

case object NoCommand extends Command