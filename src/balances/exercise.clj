(ns balances.exercise
	(:import java.util.Date))

(def accounts (ref []))

(defn create_acc
	[]
	(let [acc_numbers (map :number @accounts)]
		(dosync
			(alter accounts conj (if (empty? acc_numbers)
									{:number 1 :balance 0 :statement []}
									{:number (inc (apply max acc_numbers)) :balance 0 :statement []})))
		(println (str "Account created with number " (:number (last @accounts)) "."))
		(:number (last @accounts))))

(defn get_account
	[acc_number]
	(let [accs (drop-while #(not= acc_number (:number %)) @accounts)]
	(if (empty? accs)
		(do (println "Account " acc_number " does not exists."))
		(first accs))))

(defn do_transaction
	[account acc_number desc value date]
	(let [old_balance (:balance account)
		  old_statement (:statement account)
		  new_balance (float (+ (bigdec (:balance account)) (bigdec value)))]
		(if (= acc_number (:number account))
			(assoc account
				:balance new_balance
				:statement (conj old_statement {:desc desc :value value :date date}))
			account)))

(defn accounts_transactions
	[accounts acc_number desc value date]
	(if (some #(= acc_number %) (map :number accounts))
		(into [] (map #(do_transaction % acc_number desc value date) accounts))
		(do (println "Account " acc_number " does not exists.")
			accounts)))

(defn add_operation
	[op_type acc_number desc value date]
	(cond 
		(and (= op_type "debt") (get_account acc_number))
			(do (dosync (alter accounts accounts_transactions acc_number desc value date))
				"Success")
		(and (= op_type "credit") (get_account acc_number))
			(do (let [value (* -1 value)] (dosync (alter accounts accounts_transactions acc_number desc value date)))
				"Success")
		:else
			(do (println "Undefined operation type or wrong account number.")
				"Failure")))

(defn show_balance
	[acc_number]
	(let [account (get_account acc_number)]
		(if (nil? account)
			(println "Account " acc_number " does not exists.")
			(:balance account))))

(defn get_balance_date
	[account date]
	(float (reduce + (map bigdec (map :value (filter #(<= (compare (:date %) date) 0) (:statement account)))))))

(defn get_statement_at_date
	[account date]
	(loop [statement_list (filter #(= (compare (:date %) date) 0) (sort-by :date (:statement account)))
		   statement_at_date []]
		(let [activity (first statement_list)]
			(if-not (empty? activity)
				(do (println (str "- " (:desc activity) " " (Math/abs (:value activity))))
					(recur (rest statement_list)
						(conj statement_at_date activity)))
				(do (println (str "Balance: " (get_balance_date account date) "\n"))
					statement_at_date)))))

(defn show_statement
	[acc_number initial_date final_date]
	(let [account (get_account acc_number)]
		(if-not (nil? account)
			(let [activity_dates (sort (filter #(and (>= (compare % initial_date) 0) (<= (compare % final_date) 0)) (set (map :date (:statement account)))))]
				(loop [dates activity_dates
					   statement []]
					(let [date (first dates)]
						(if-not (nil? date)
							(do (println (.format (java.text.SimpleDateFormat. "dd/MM:") date))
								(recur (rest dates)
									(conj statement {:date date :statement (map #(select-keys % [:desc :value]) (get_statement_at_date account date)) :balance (get_balance_date account date)})))
							statement))))
			(println "Account " acc_number " does not exists."))))

(defn balance_history
	[account]
	(let [activity_dates (sort (set (map :date (:statement account))))]
		(for [date activity_dates]
			(hash-map :date date :balance (get_balance_date account date)))))

(defn add_days
	[date days]
	(java.util.Date. (+ (* days 86400 1000) (.getTime date))))

(defn debt_period
	[initial final]
	(if (< (:balance initial) 0)
		(if (nil? final)
			(do (println (str "- Principal: " (Math/abs (:balance initial)) "\n  Start: " (.format (java.text.SimpleDateFormat. "dd/MM") (:date initial)) "\n"))
				(hash-map :principal (Math/abs (:balance initial)), :start (:date initial)))
			(do (println (str "- Principal: " (Math/abs (:balance initial)) "\n  Start: " (.format (java.text.SimpleDateFormat. "dd/MM") (:date initial)) "\n  End: " (.format (java.text.SimpleDateFormat. "dd/MM") (add_days (:date final) -1)) "\n"))
				(hash-map :principal (Math/abs (:balance initial)), :start (:date initial), :end (add_days (:date final) -1))))))

(defn debt_periods
	[acc_number]
	(let [account (get_account acc_number)]
		(if-not (nil? account)
			(loop [balance_history (sort-by :date (balance_history account))
				   debits []]
				(if-not (nil? (first balance_history))
					(recur (rest balance_history)
						(remove nil? (conj debits (debt_period (first balance_history) (second balance_history)))))
					debits))
			(println "Account " acc_number " does not exists."))))

(defn parse_date
	[date_string]
	(.parse (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss") date_string))