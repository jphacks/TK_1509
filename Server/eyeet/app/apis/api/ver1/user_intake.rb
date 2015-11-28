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
          data = {"recomended_cal"=> user.recommended_daily_calorie, "recomended_protein" => user.recommended_daily_protein}

        #TODO: 今日の期間内の摂取量を計算



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
