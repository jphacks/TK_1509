module API
  module Ver1
    class Root < Grape::API
      # http://localhost:3000/api/v1/
      version 'v1'
      format :json

     # mount API::Ver1::Users
      mount API::Ver1::Meals
      mount API::Ver1::UserIntake
    end
  end
end