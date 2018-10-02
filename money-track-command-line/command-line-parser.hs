import System.Environment

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