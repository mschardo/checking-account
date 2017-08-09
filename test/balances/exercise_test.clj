(ns balances.exercise-test
  (:use clojure.test
        balances.exercise))

(defn compare_statements
	[a b]
	[(= (:date [a b])) (= (:statement [a b])) (= (:balance [a b]))])

(defn compare_debits
	[a b]
	[(= (:start [a b])) (= (:end [a b]))])

(testing "Main program basic test"
	(is (create_acc) {:number 1})
	(let [account (get_account 1)]
		(is (not (nil? account)))
		(is (= "Success"
			   (add_operation "debt" 1 "Deposit" 1000.00 (parse_date "2017-10-15 00:00:00"))))
		(is (= "Success"
			   (add_operation "credit" 1 "Purchase on Amazon" 3.34 (parse_date "2017-10-16 00:00:00"))))
		(is (= "Success"
			   (add_operation "credit" 1 "Purchase on Uber" 45.23 (parse_date "2017-10-16 00:00:00"))))
		(is (= "Success"
			   (add_operation "credit" 1 "Withdrawal" 180.00 (parse_date "2017-10-17 00:00:00"))))
		(is (= (float 771.43)
			   (show_balance 1)))
		(is (= (map compare_statements [{:date (parse_date "2017-10-15 00:00:00"), :statement [{:desc "Deposit", :value (float 1000.0)}], :balance (float 1000.0)} {:date (parse_date "2017-10-16 00:00:00"), :statement [{:desc "Purchase on Amazon", :value (float -3.34)} {:desc "Purchase on Uber", :value (float -45.23)}], :balance (float 951.43)} {:date (parse_date "2017-10-17 00:00:00"), :statement [{:desc "Withdrawal", :value (float -180.0)}], :balance (float 771.43)}]
			   (show_statement 1 (parse_date "2017-10-14 00:00:00") (parse_date "2017-10-18 00:00:00")))))
		(is (= "Success"
			   (add_operation "credit" 1 "Purchase of a flight ticket" 800.00 (parse_date "2017-10-18 00:00:00"))))
		(is (= "Success"
			   (add_operation "debt" 1 "Deposit" 100.00 (parse_date "2017-10-25 00:00:00"))))
		(is (= (map compare_debits [{:start (parse_date "2017-10-18 00:00:00"), :end (parse_date "2017-10-24 00:00:00"), :principal (float 28.57)}] (debt_periods 1))))))

(testing "Main program advanced test"
	(is (create_acc) {:number 1})
	(let [account (get_account 1)]
		(is (not (nil? account)))
		(is (= "Success"
			   (add_operation "credit" 1 "Payment made" 100.00 (parse_date "2017-10-30 00:00:00"))))
		(is (= "Success"
			   (add_operation "credit" 1 "Payment made" 100.00 (parse_date "2017-10-30 00:00:00"))))
		(is (= "Success"
			   (add_operation "credit" 1 "Payment made" 100.00 (parse_date "2017-11-03 00:00:00"))))
		(is (= "Success"
			   (add_operation "debt" 1 "Deposit" 1000.00 (parse_date "2017-11-08 00:00:00"))))
		(is (= (map compare_statements [{:date (parse_date "2017-10-15 00:00:00"), :statement [{:desc "Deposit", :value 1000.0}], :balance 1000.0} {:date (parse_date "2017-10-16 00:00:00"), :statement [{:desc "Purchase on Amazon", :value -3.34} {:desc "Purchase on Uber", :value -45.23}], :balance 951.43} {:date (parse_date "2017-10-17 00:00:00"), :statement [{:desc "Withdrawal", :value -180.0}], :balance 771.43} {:date (parse_date "2017-10-18 00:00:00"), :statement [{:desc "Purchase of a flight ticket", :value -800.0}], :balance -28.57} {:date (parse_date "2017-10-25 00:00:00"), :statement [{:desc "Deposit", :value 100.0}], :balance 71.43} {:date (parse_date "2017-10-30 00:00:00"), :statement [{:desc "Payment made", :value -100.0} {:desc "Payment made", :value -100.0}], :balance -128.57} {:date (parse_date "2017-11-03 00:00:00"), :statement [{:desc "Payment made", :value -100.0}], :balance -228.57} {:date (parse_date "2017-11-08 00:00:00"), :statement [{:desc "Deposit", :value 1000.0}], :balance 771.43}]
			   (show_statement 1 (parse_date "2017-10-14 00:00:00") (parse_date "2017-11-10 00:00:00")))))
		(is (= (map compare_debits [{:start (parse_date "2017-11-03 00:00:00"), :end (parse_date "2017-11-07 00:00:00"), :principal 228.57} {:start (parse_date "2017-10-30 00:00:00"), :end (parse_date "2017-11-02 00:00:00"), :principal 128.57} {:start (parse_date "2017-10-18 00:00:00"), :end (parse_date "2017-10-24 00:00:00"), :principal 28.57}] (debt_periods 1))))
		(is (= "Success"
			   (add_operation "credit" 1 "Payment made" 2000.00 (parse_date "2017-11-10 00:00:00"))))
		(is (= (map compare_debits [{:start (parse_date "2017-11-10 00:00:00"), :principal 1228.57} {:start (parse_date "2017-11-03 00:00:00"), :end (parse_date "2017-11-07 00:00:00"), :principal 228.57} {:start (parse_date "2017-10-30 00:00:00"), :end (parse_date "2017-11-02 00:00:00"), :principal 128.57} {:start (parse_date "2017-10-18 00:00:00"), :end (parse_date "2017-10-24 00:00:00"), :principal 28.57}] (debt_periods 1))))))
(run-tests)