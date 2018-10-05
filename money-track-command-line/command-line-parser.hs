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

executeCommand :: Command -> String
executeCommand UnknownCommand = "The command is unknown!"
executeCommand ShowCommands = commandUsage
executeCommand (GetAmount days) = "x money"
executeCommand (Remove (Transaction name amount date category)) = "Transaction removed"
executeCommand (Add (Transaction name amount date category)) = "Transaction added"
executeCommand (GetTransactions days) = "X Transactions per last n days"
executeCommand (GetTransactionsByInterval start end) = "transactions per interval"
executeCommand (GetTransactionsByDate date) = "transactions by date"
executeCommand (GetAmountByInterval start end) = "amount per interval"
executeCommand (GetAmountForDate date) = "amount per date"

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


getStartDate :: [(String, String)] -> Maybe String
getStartDate args = find args (\x -> x == "--s")

getEndDate :: [(String, String)] -> Maybe String
getEndDate args = find args (\x -> x == "--e")

isDefined :: Maybe a -> Bool
isDefined Nothing = False
isDefined (Just a) = True

unwrap :: Maybe String -> String
unwrap (Just s) = s


parseForTransaction :: (Transaction -> Command) -> [(String, String)] -> Command
parseForTransaction command args = case getTransaction args of 
				(Just t) -> command t
				Nothing  -> UnknownCommand

parseForGetAmount :: [(String, String)] -> Command
parseForGetAmount args 
	| isForInterval     = GetAmountByInterval (unwrap start) (unwrap end)
	| isForSpecificDate = GetAmountForDate (unwrap date)
	| otherwise         = UnknownCommand
	where 
		start = getArgument "--s" args
		end = getArgument "--e" args
		date = getArgument "--d" args
		isForInterval = (isDefined start) && (isDefined end)
		isForSpecificDate = isDefined date


parseForGetTransactions :: [(String, String)] -> Command
parseForGetTransactions args 
	| isForInterval     = GetTransactionsByInterval (unwrap start) (unwrap end)
	| isForSpecificDate = GetTransactionsByDate (unwrap date)
	| otherwise         = UnknownCommand
	where 
		start = getArgument "--s" args
		end = getArgument "--e" args
		date = getArgument "--d" args
		isForInterval = (isDefined start) && (isDefined end)
		isForSpecificDate = isDefined date

parseForHistory :: (Int -> Command) -> [(String, String)] -> Command
parseForHistory command args = case (getArgument "--h" args) of
				Nothing -> UnknownCommand
				Just n  -> command (read n::Int)


classifyFirstArgument :: String -> [(String, String)] -> Command
classifyFirstArgument "--add"       = parseForTransaction Add
classifyFirstArgument "--getAmount" = parseForGetAmount
classifyFirstArgument "--get"       = parseForGetTransactions
classifyFirstArgument "--l"         = parseForHistory GetTransactions
classifyFirstArgument "--la"        = parseForHistory GetAmount
classifyFirstArgument "--remove"    = parseForTransaction Remove
classifyFirstArgument _             = (\x -> UnknownCommand)

classifyToCommand :: [(String, String)] -> Command
classifyToCommand []   = ShowCommands
classifyToCommand args = (classifyFirstArgument $ fst $ head args) (tail args)




splitAtFirst :: Char -> String -> (String, String)
splitAtFirst _ "" = ("", "")
splitAtFirst c s = 
	if ((head s) == c ) then ("", tail s)
	else ((head s) : first, second)
	where (first, second) = splitAtFirst c (tail s)
		

parse :: [String] -> [(String, String)]
parse input = map (\x -> splitAtFirst '=' x) input


main = do
	args <- getArgs
	print (classifyToCommand (parse args))