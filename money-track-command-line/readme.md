The command line app supports the following commands:
    => money-track --add --a=${amount} --n=${name} --c=${category} --d=${date}
        - date is optional, with a default to current day

    => money-track --get-amount -d=${date} - get the amount spent for a specific day


    => money-track --get-amount -s=${date} --e=${date} - get the amount spent for a specific period


    => money-track --get --d=${date} - retrieves the transactions for a specific date


    => money-track --get --s=${date} --e=${date} - retrieves the transactions for a specific period


    => money-track --l=${n} - retrieves the transactions for the past n days


    => money-track -remove --d=${date} --n=${name} --c=${category} --a=${amount}
                            => since the amount is mandatory, it is crucial to know exactly what is to be deleted
                            => the easiest way to do this is to do a get by a day first and then delete a specific one
