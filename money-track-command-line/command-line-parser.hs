import System.Environment
import Data.Time
import Data.Maybe


data Transaction = Transaction {
				name   :: String,
				amount :: Double,
				date   :: String,
				category :: String

				} deriving Show

data Command =  GetAmountForDate String | 
		GetAmountByInterval String String |
		GetTransactionsByDate String | 
		GetTransactionsByInterval String String | 
		GetTransactions Int | 
		GetAmount Int | 
		Add Transaction | 
		Remove Transaction |
		ShowCommands |
		UnknownCommand deriving Show

commandUsage = ""


splitAtFirst :: Char -> String -> (String, String)
splitAtFirst _ "" = ("", "")
splitAtFirst c s = 
	if ((head s) == c ) then ("", tail s)
	else ((head s) : first, second)
	where (first, second) = splitAtFirst c (tail s)
		

parse :: [String] -> [(String, String)]
parse input = map (\x -> splitAtFirst '=' x) input


find :: [(String, String)] -> (String -> Bool) -> Maybe String
find [] _= Nothing
find args f = if (f currArg) then Just value
		else find (tail args) f
		where currArg = fst $ head $ args
		      value = snd $ head $ args

getArgument :: String -> [(String, String)] -> Maybe String
getArgument arg args = find args (\x -> x == arg)

getTransaction :: [(String, String)] -> Maybe Transaction
getTransaction args = do
			name     <- getArgument "--n" args
			amount   <- getArgument "--a" args
			date     <- getArgument "--d" args
			category <- getArgument "--c" args
			Just (Transaction name (read amount::Double) date category)

isDefined :: Maybe a -> Bool
isDefined Nothing = False
isDefined (Just a) = True

unwrap :: Maybe String -> String
unwrap (Just s) = s


parseForTransaction :: (Transaction -> Command) -> [(String, String)] -> Maybe Command
parseForTransaction command args = fmap (\x -> command x) (getTransaction args)

parseForDates :: (String -> String -> Command) -> (String -> Command) -> [(String, String)] -> Maybe Command
parseForDates byInterval byDate args 
	| isForInterval     = Just (byInterval (unwrap start) (unwrap end))
	| isForSpecificDate = Just (byDate (unwrap date))
	| otherwise         = Nothing
	where 
		start = getArgument "--s" args
		end = getArgument "--e" args
		date = getArgument "--d" args
		isForInterval = (isDefined start) && (isDefined end)
		isForSpecificDate = isDefined date


parseForHistory :: (Int -> Command) -> [(String, String)] -> Maybe Command
parseForHistory command args = fmap (\x -> command (read x::Int)) (getArgument "--h" args)


classifyFirstArgument :: String -> [(String, String)] -> Maybe Command
classifyFirstArgument "--add"       = parseForTransaction Add
classifyFirstArgument "--getAmount" = parseForDates GetAmountByInterval GetAmountForDate
classifyFirstArgument "--get"       = parseForDates GetTransactionsByInterval GetTransactionsByDate
classifyFirstArgument "--l"         = parseForHistory GetTransactions
classifyFirstArgument "--la"        = parseForHistory GetAmount
classifyFirstArgument "--remove"    = parseForTransaction Remove
classifyFirstArgument _             = (\x -> Just(UnknownCommand))

classifyToCommand :: [(String, String)] -> Maybe Command
classifyToCommand []   = Just ShowCommands
classifyToCommand args = (classifyFirstArgument $ fst $ head args) (tail args)

executeCommand :: Maybe Command -> String
executeCommand command = case command of
				Just c -> go c
				Nothing -> "The command has an invalid format!"

go :: Command -> String
go UnknownCommand = "The command is unknown!"
go ShowCommands = commandUsage
go (GetAmount days) = "x money"
go (Remove (Transaction name amount date category)) = "Transaction removed"
go (Add (Transaction name amount date category)) = "Transaction added"
go (GetTransactions days) = "X Transactions per last n days"
go (GetTransactionsByInterval start end) = "transactions per interval"
go (GetTransactionsByDate date) = "transactions by date"
go (GetAmountByInterval start end) = "amount per interval"
go (GetAmountForDate date) = "amount per date"


main = do
	args <- getArgs
	let result = executeCommand (classifyToCommand (parse args))
	print result