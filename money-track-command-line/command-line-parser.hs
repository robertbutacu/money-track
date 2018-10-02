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