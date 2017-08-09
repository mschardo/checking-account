(ns balances.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [balances.exercise :refer :all]
            [schema.core :as s])
  (:import java.util.Date))

(load "exercise")

; A schema for 'create account' service response.
(s/defschema DateYearMonthDay
  {:day s/Int
   :month s/Int
   :year s/Int})

; A schema for 'create account' service response.
(s/defschema CreateAccountResponse
  {:acc_num s/Int})

; A schema for 'add operation' service request.
(s/defschema AddOperationRequest
  {:type (s/enum "debt" "credit")
   :acc_num s/Int
   :desc s/Str
   :value s/Num
   :date DateYearMonthDay})

; A schema for 'add operation' service response.
(s/defschema AddOperationResponse
  {:status (s/enum "Success" "Failure")})

; A schema for 'show balance' service request.
(s/defschema ShowBalanceRequest
  {:acc_num s/Int})

; A schema for 'show balance' service response.
(s/defschema ShowBalanceResponse
  {:balance s/Num})

; A schema for 'show statement' service request.
(s/defschema ShowStatementRequest
  {:acc_num s/Int
   :initial_date DateYearMonthDay
   :final_date DateYearMonthDay})

; A schema for 'show statement' service response.
(s/defschema ShowStatementResponse
  {:response [{:date s/Inst
    :statement [{:desc s/Str
                 :value s/Num}]
    :balance s/Num}]})

; A schema for 'show debt' service request.
(s/defschema ShowDebtRequest
  {:acc_num s/Int})

; A schema for 'show debt' service response.
(s/defschema ShowDebtResponse
  {:response [{:start s/Inst
    (s/optional-key :end) s/Inst
    :principal s/Num}]})

(defn get_date
  [dateMap]
  (parse_date (format "%4d-%02d-%02d 00:00:00" (:year dateMap) (:month dateMap) (:day dateMap))))

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Balances"
                    :description "API for Exercise Balances"}
             :tags [{:name "api", :description "Basic features of a checking account"}]}}}

    (context "/api" []
      :tags ["api"]

      (GET "/create" []
        :return CreateAccountResponse
        :query-params []
        :summary "Create an account. Returns new account number."
        (ok {:acc_num (create_acc)}))

      (POST "/balance" []
        :return ShowBalanceResponse
        :body [request ShowBalanceRequest]
        :summary "Show an account's balance."
        (ok {:balance (show_balance (:acc_num request))}))

      (POST "/operation" []
        :return AddOperationResponse
        :body [request AddOperationRequest]
        :summary "Add an operation to the account."
        (ok {:status (add_operation (:type request) (:acc_num request) (:desc request) (:value request) (get_date (:date request)))}))

      (POST "/statement" []
        :return ShowStatementResponse
        :body [request ShowStatementRequest]
        :summary "Show an account's statement."
        (ok {:response (show_statement (:acc_num request) (get_date (:initial_date request)) (get_date (:final_date request)))}))

      (POST "/debt" []
        :return ShowDebtResponse
        :body [request ShowDebtRequest]
        :summary "Show an account's debt periods."
        (ok {:response (debt_periods (:acc_num request))})))))
