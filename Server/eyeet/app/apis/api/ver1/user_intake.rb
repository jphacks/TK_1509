require "faraday"
require "json"
require "pp"

module API
  module Ver1
    class UserIntake < Grape::API
      resource :user_intake do

        # GET /api/v1/user_intake/:user_id
       get ':user_id' do
        begin
          user = User.find_by_id(params[:user_id])

          #カロリー
          recomended_cal = user.recommended_daily_calorie
          today_intake_cal = 0.0
          rate_cal = 0.0

          #タンパク質
          recomended_protein = user.recommended_daily_protein
          today_intake_protein = 0.0
          rate_protein = 0.0

        #今日の期間内の摂取量を計算
        nutrition = {}
        today_intakes = Food.where(created_at: Time.now.midnight..Time.now, user_id: params[:user_id])
        today_intakes.each do | food |

           nut =  Nutrition.find_by_name(food.name)
           if nut.present?
              nutrition =  {"calorie" => nut['calorie'], "protein"=> nut['protein'], "fat" => nut['fat'], "carb"=> nut['carb'], "vitamin"=> nut['vitamin'], "mineral"=> nut['mineral']}
               today_intake_cal += nut['calorie'].to_f
               today_intake_protein += nut['protein'].to_f
           else #デバッグ用 栄養DBなかったとき用 ポテチ（仮）
              nutrition =  {"calorie" => 335, "protein"=> 2.8, "fat" => 21.4, "carb"=> 32.8, "vitamin"=> {}, "mineral"=> {"Na"=>284} }
               today_intake_cal += 335
               today_intake_protein += 2.8
           end

          end

          rate_cal = today_intake_cal * 100.0 / recomended_cal
          rate_protein = today_intake_protein * 100.0 / recomended_protein

          data = {
            "intake_cal"=> today_intake_cal.round(2),
            "recomended_cal"=> recomended_cal,
            "rate_cal"=> rate_cal.round(2),
            "intake_protein"=> today_intake_protein.round(2),
            "recomended_protein"=> recomended_protein,
            "rate_protein"=> rate_protein.round(2)
          }

          status = "OK"
        rescue
          status = "Error"
        end

        {"data"=>data, "status"=>status}
        end

      end
    end
  end
end
