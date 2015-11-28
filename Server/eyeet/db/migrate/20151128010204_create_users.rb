class CreateUsers < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.boolean :man
      t.string :name
      t.date :birthdate
      t.integer :height
      t.integer :weight
      t.string :email

      t.timestamps null: false
    end
  end
end
