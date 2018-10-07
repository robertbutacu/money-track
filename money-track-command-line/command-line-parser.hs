{-# LANGUAGE OverloadedStrings, DeriveGeneric #-}

import System.Environment
import Data.Time
import Data.Maybe
import Data.Aeson
import GHC.Generics
import Data.Foldable
import Control.Monad
import Network
import Network.HTTP.Conduit
import Network.HTTP.Types.Header
import qualified Data.ByteString.Lazy as B

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

extractRequest :: Maybe Command -> Maybe RequestD
extractRequest command = flatten (fmap go command)

go :: Command -> Maybe RequestD
go UnknownCommand = Nothing
go ShowCommands = Nothing
go (GetAmount days) = Just $ GetAmountRequest "GET" ("http://localhost:8080/amount/last?days=" ++ (show days))
go (Remove t) = Just $ PerformOperationRequest "GET" "http://localhost" t
go (Add t) = Just $ PerformOperationRequest "GET" "http://localhost" t
go (GetTransactions days) = Just $ GetTransactionsRequest "GET" "http://localhost"
go (GetTransactionsByInterval start end) = Just $ GetTransactionsRequest "GET" "http://localhost"
go (GetTransactionsByDate date) = Just $ GetTransactionsRequest "GET" ("http://localhost:8080/amount/last?days=" ++ (show date))
go (GetAmountByInterval start end) = Just $ GetAmountRequest "GET" ("http://localhost:8080/amount/last?days=" ++ (show start))
go (GetAmountForDate date) = Just $ GetAmountRequest "GET" ("http://localhost:8080/transactions?date=" ++ (show date))

data RequestD = GetAmountRequest {
		requestMethod :: String,
		url :: String
		} |
		GetTransactionsRequest {
		requestMethod :: String,
		url :: String
		} |
		PerformOperationRequest {
		requestMethod :: String,
		url :: String,
		transaction :: Transaction 
		} deriving Show

toString :: Int -> Transaction -> String
toString index (Transaction n a d c) = (show index) ++ ".   Name: " ++ n ++ "   Amount: " ++ (show a) ++ "  Date:  " ++ d ++ "   Category: " ++ c ++ "\n"

zipWithIndex :: [a] -> [(Int, a)]
zipWithIndex l = zip [0..] l

prettyPrinter :: [Transaction] -> String
prettyPrinter t = foldl (\x y -> x ++ (toString (fst y) (snd y))) "" (zipWithIndex t)

--executeRequest :: RequestD -> B.ByteString
--executeRequest (GetAmountRequest method url) = rspBody $ simpleHttp method url

--executeRequest (GetTransactionsRequest method url) = return ""
--executeRequest (PerformOperationRequest method url transaction) = return ""

--processRequestForHistory :: String -> IO [Transaction]

--processRequestForAmount :: String -> IO Double

--processRequestForOperation :: String -> IO ()

query :: IO String
query = do
    initReq <- parseUrl "http://localhost:8080/amount/last"
    let r = initReq { method = "GET", requestHeaders = []}
    let request = setQueryString [("days", Just "23000")] r
    manager <- newManager tlsManagerSettings
    res <- httpLbs request manager
    return . show . responseBody $ res


main = do
	args <- getArgs
	let request = extractRequest (classifyToCommand (parse args))
	--let response = fmap executeRequest request
	query >>= putStrLn