import System.Environment
import Data.Time
import Data.Maybe


data Transaction = Transaction {
				name   :: String,
				amount :: Double,
				date   :: String,
				category :: Maybe String

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

getName :: [(String, String)] -> String
getName args = case (find args (\x -> x == "--n")) of 
		Nothing -> ""
		(Just v) -> v

getAmount :: [(String, String)] -> Double
getAmount args = case (find args (\x -> x == "--a")) of 
		Nothing -> 0.0
		(Just v) -> read v :: Double

getDate :: [(String, String)] -> String
getDate args = case (find args (\x -> x == "--d")) of 
		Nothing -> ""
		(Just v) -> v

getStartDate :: [(String, String)] -> Maybe String
getStartDate args = find args (\x -> x == "--s")

getEndDate :: [(String, String)] -> Maybe String
getEndDate args = find args (\x -> x == "--e")

getCategory :: [(String, String)] -> Maybe String
getCategory args = find args (\x -> x == "--c")

isDefined :: Maybe a -> Bool
isDefined Nothing = False
isDefined (Just a) = True

unwrap :: Maybe String -> String
unwrap (Just s) = s

getTransaction :: [(String, String)] -> Transaction
getTransaction args = 
	Transaction (getName args) (getAmount args) (getDate args) (getCategory args)

parseForAddTransaction :: [(String, String)] -> Command
parseForAddTransaction _ = ShowCommands

parseForGetAmount :: [(String, String)] -> Command
parseForGetAmount args 
	| isForInterval     = GetAmountByInterval (unwrap start) (unwrap end)
	| isForSpecificDate = GetAmountForDate (unwrap date)
	| otherwise         = UnknownCommand
	where 
		start = find args (\x -> x == "--s")
		end = find args (\x -> x == "--e")
		date = find args (\x -> x == "--d")
		isForInterval = (isDefined start) && (isDefined end)
		isForSpecificDate = isDefined date


parseForGetTransactions :: [(String, String)] -> Command
parseForGetTransactions _ = ShowCommands

parseForTransactionHistory :: [(String, String)] -> Command
parseForTransactionHistory _ = ShowCommands

parseForAmountHistory :: [(String, String)] -> Command
parseForAmountHistory _ = ShowCommands

parseForTransactionRemoval :: [(String, String)] -> Command
parseForTransactionRemoval _ = ShowCommands

classifyFirstArgument :: String -> [(String, String)] -> Command
classifyFirstArgument "--add"       = parseForAddTransaction
classifyFirstArgument "--getAmount" = parseForGetAmount
classifyFirstArgument "--get"       = parseForGetTransactions
classifyFirstArgument "--l"         = parseForTransactionHistory
classifyFirstArgument "--la"        = parseForAmountHistory
classifyFirstArgument "--remove"    = parseForTransactionRemoval
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