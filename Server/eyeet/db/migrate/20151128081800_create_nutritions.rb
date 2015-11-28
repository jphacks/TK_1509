class CreateNutritions < ActiveRecord::Migration
  def change
    create_table :nutritions do |t|
      t.string :name
      t.decimal :calorie
      t.decimal :fat
      t.decimal :carb
      t.decimal :protein
      t.text :vitamin
      t.text :mineral

      t.timestamps null: false
    end
  end
end
