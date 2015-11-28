class User < ActiveRecord::Base
   has_many :meals
   has_many :foods
end
