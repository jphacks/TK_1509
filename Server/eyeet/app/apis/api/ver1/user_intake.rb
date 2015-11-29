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

          #脂質
          recomended_fat = user.recommended_daily_fat
          today_intake_fat = 0.0
          rate_fat = 0.0

          #炭水化物
          recomended_carb = user.recommended_daily_carb
          today_intake_carb = 0.0
          rate_carb = 0.0


        #今日の期間内の摂取量を計算
        nutrition = {}
        today_intakes = Food.where(created_at: Time.now.midnight..Time.now, user_id: params[:user_id])
        today_intakes.each do | food |

           nut =  Nutrition.find_by_name(food.name)
           if nut.present?
              nutrition =  {"calorie" => nut['calorie'], "protein"=> nut['protein'], "fat" => nut['fat'], "carb"=> nut['carb'], "vitamin"=> nut['vitamin'], "mineral"=> nut['mineral']}
               today_intake_cal += nut['calorie'].to_f
               today_intake_protein += nut['protein'].to_f
               today_intake_fat += nut['fat'].to_f
               today_intake_carb += nut['carb'].to_f
           #   today_intake_mineral += nut['mineral'].to_f
           else #デバッグ用 栄養DBなかったとき用 ポテチ（仮）
              nutrition =  {"calorie" => 335, "protein"=> 2.8, "fat" => 21.4, "carb"=> 32.8, "vitamin"=> {}, "mineral"=> {"Na"=>284} }
               today_intake_cal += 335
               today_intake_protein += 2.8
               today_intake_fat += 21.4
               today_intake_carb += 32.8
           end

          end

          rate_cal = today_intake_cal * 10 / recomended_cal
          rate_protein = today_intake_protein * 10 / recomended_protein
          rate_fat = today_intake_fat * 10 / recomended_fat
          rate_carb = today_intake_carb * 10 / recomended_carb

          if rate_cal > 10
            rate_cal = 10
          end
          if rate_protein > 10
            rate_protein = 10
          end
          if rate_fat > 10
            rate_fat = 10
          end
          if rate_carb > 10
            rate_carb = 10
          end


          data = {
            "intake_cal"=> today_intake_cal.round(2),
            "recomended_cal"=> recomended_cal,
            "rate_cal"=> rate_cal.round(0),
            "intake_protein"=> today_intake_protein.round(2),
            "recomended_protein"=> recomended_protein,
            "rate_protein"=> rate_protein.round(0),
            "intake_fat"=> today_intake_fat.round(2),
            "recomended_fat"=> recomended_fat,
            "rate_fat"=> rate_fat.round(0),
            "intake_carb"=> today_intake_carb.round(2),
            "recomended_carb"=> recomended_carb,
            "rate_carb"=> rate_carb.round(0)
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
