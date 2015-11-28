json.array!(@nutritions) do |nutrition|
  json.extract! nutrition, :id, :name, :calorie, :fat, :carb, :protein, :vitamin, :mineral
  json.url nutrition_url(nutrition, format: :json)
end
