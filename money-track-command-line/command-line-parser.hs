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
		Remove Transaction


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

getCategory :: [(String, String)] -> Maybe String
getCategory args = find args (\x -> x == "--c")


getTransaction :: [(String, String)] -> Transaction
getTransaction args = 
	Transaction (getName args) (getAmount args) (getDate args) (getCategory args)


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
	print (getTransaction (parse args))