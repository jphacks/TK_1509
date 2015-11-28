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
               Meal.create({
                    user_id: params[:user_id],
                    image: params[:image]
                })
               return "ok"
            rescue #=> e
                return "failed"
            end
        end

      end
    end
  end
end