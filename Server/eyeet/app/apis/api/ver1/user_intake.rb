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
          recomended_cal = user.recommended_daily_calorie
          today_intake_cal = 0.0
          rate_cal = 0.0

        #TODO: 今日の期間内の摂取量を計算
        nutrition = {}
        today_intakes = Food.where(created_at: Time.now.midnight..Time.now, user_id: params[:user_id])
        today_intakes.each do | food |

           nut =  Nutrition.find_by_name(food.name)
           if nut.present?
              nutrition =  {"calorie" => nut['calorie'], "protein"=> nut['protein'], "fat" => nut['fat'], "carb"=> nut['carb'], "vitamin"=> nut['vitamin'], "mineral"=> nut['mineral']}
               today_intake_cal += nut['calorie'].to_f
               puts nut['calorie'].to_f
               puts "goeeeeee"
               puts today_intake_cal
           end

          end

          rate_cal = today_intake_cal * 100.0 / recomended_cal

          data = {
            "intake_cal"=> today_intake_cal,
            "recomended_cal"=> recomended_cal,
            "rate_cal"=> rate_cal.round(2),
            "recomended_protein" => user.recommended_daily_protein
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
