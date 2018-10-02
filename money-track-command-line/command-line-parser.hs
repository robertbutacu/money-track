import System.Environment
import Data.Time
import Data.Maybe


data Transaction = Transaction {
				name   :: String,
				amount :: Double,
				date   :: String,
				category :: Maybe String

				}

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
find args f = if (f currArg) then Just currArg
		else find (tail args) f
		where currArg = fst $ head $ args

--getTransaction :: [(String, String)] -> Transaction
--getTransaction args = 


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
	print (parse args)
	print (find (parse args) (\s -> (length s) > 5))