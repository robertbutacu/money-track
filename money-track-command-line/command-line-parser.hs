{-# LANGUAGE OverloadedStrings, DeriveGeneric #-}

import System.Environment
import Data.Time
import Data.Maybe
import Data.Aeson
import GHC.Generics
import Data.Foldable
import Control.Monad.Fix

data Transaction = Transaction {
				name   :: String,
				amount :: Double,
				date   :: String,
				category :: String

				} deriving (Show, Generic)

instance FromJSON Transaction
instance ToJSON Transaction

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

getArgument :: String -> [(String, String)] -> Maybe String
getArgument arg args = fmap snd $ find (\x -> (fst x) == arg) args

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

flatten :: Maybe (Maybe a) -> Maybe a
flatten input = case input of 
		Just v  -> case v of
				Just result -> Just result
				Nothing     -> Nothing
		Nothing -> Nothing

executeCommand :: Maybe Command -> Maybe Request
executeCommand command = flatten (fmap go command)

go :: Command -> Maybe Request
go UnknownCommand = Nothing
go ShowCommands = Nothing
go (GetAmount days) = Just $ Request "GET" ("http://localhost:8080/amount/last?days=" ++ (show days))
go (Remove (Transaction name amount date category)) = Just $ Request "GET" "http://localhost"
go (Add (Transaction name amount date category)) = Just $ Request "GET" "http://localhost"
go (GetTransactions days) = Just $ Request "GET" "http://localhost"
go (GetTransactionsByInterval start end) = Just $ Request "GET" "http://localhost"
go (GetTransactionsByDate date) = Just $ Request "GET" ("http://localhost:8080/amount/last?days=" ++ (show date))
go (GetAmountByInterval start end) = Nothing--Just $ Request "GET" ("http://localhost:8080/amount/last?days=" ++ (show days))
go (GetAmountForDate date) = Just $ Request "GET" ("http://localhost:8080/transactions?date=" ++ (show date))

data Request = Request {
		method :: String,
		url :: String
		} deriving Show

--processRequestForHistory :: String -> IO [Transaction]

--processRequestForAmount :: String -> IO Double

--processRequestForOperation :: String -> IO ()

main = do
	args <- getArgs
	let result = executeCommand (classifyToCommand (parse args))
	print result