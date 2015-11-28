require "faraday"
require "json"
require "pp"

module API
  module Ver1
    class Meals < Grape::API
      resource :meals do

        # GET /api/v1/meals
        get do
          Meal.all
        end

        # POST /api/v1/meals
        params do
          requires :image, type: Hash
          requires :user_id, type: String
        end
        post do
            begin
                result = {"data"=>[], "status"=>""}

               new_meal = Meal.create({
                    user_id: params[:user_id],
                    image: params[:image]
                })

                full_path = "#{Rails.root}" + "/public"+ new_meal.image.url
               image_file = File.open(full_path, "r+b")

                client = Faraday.new(:url => "https://api.apigw.smt.docomo.ne.jp")
                res = client.post do |req|
                  req.url '/imageRecognition/v1/recognize?APIKEY=572e78732e47743935372e6a5838787961304446755a61467a654c564734346c7770376356797036636632&recog=food&numOfCandidates=2'
                  req.headers['Content-Type'] = 'application/octet-stream'
                  req.body = image_file.read
                end
                body = JSON.parse res.body
                body['candidates'].each do |candidate|
                    result["data"].push(candidate)
                end
                result["status"] = "OK"
            rescue => e
               result["status"] = "Error"
            end
            result

        end

      end
    end
  end
end