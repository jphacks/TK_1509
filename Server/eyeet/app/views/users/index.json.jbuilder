json.array!(@users) do |user|
  json.extract! user, :id, :man, :name, :birthdate, :height, :weight, :email
  json.url user_url(user, format: :json)
end
