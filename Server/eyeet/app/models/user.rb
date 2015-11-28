class User < ActiveRecord::Base
   has_many :meals
   has_many :foods

  def age
    d1=self.birthdate.strftime("%Y%m%d").to_i
    d2=Date.today.strftime("%Y%m%d").to_i
    return (d2 - d1) / 10000
  end


 #http://www.mhlw.go.jp/file/04-Houdouhappyou-10904750-Kenkoukyoku-Gantaisakukenkouzoushinka/0000041955.pdf
 def recommended_daily_calorie #推定エネルギー必要表（kcal/日）
   #TODO：運動量に合わせて変動させる
   if self.man
	case self.age #男性の年齢別
	   when 0 then 700
	   when 1..2 then 950
	   when 3..5 then 1300
	   when 6..7 then 1550
	   when 8..9 then 1850
	   when 10..11 then 2250
	   when 12..14 then 2600
	   when 15..17 then 2850
	   when 18..29 then 2650
	   when 30..49 then 2650
	   when 50..69 then 2450
	   when 70..150 then 2200
	   else 2200
	end
   else
	case self.age #女性の年齢別
	   when 0 then 650
	   when 1..2 then 900
	   when 3..5 then 1250
	   when 6..7 then 1450
	   when 8..9 then 1700
	   when 10..11 then 2100
	   when 12..14 then 2400
	   when 15..17 then 2300
	   when 18..29 then 1950
	   when 30..49 then 2000
	   when 50..69 then 1900
	   when 70..150 then 1750
	   else 1750
	end
   end

  end

def recommended_daily_protein #タンパク質摂取 推奨表（g日）
   #TODO：運動量に合わせて変動させる
   if self.man
	case self.age
	   when 0 then 25
	   when 1..2 then 20
	   when 3..5 then 25
	   when 6..7 then 35
	   when 8..9 then 40
	   when 10..11 then 50
	   when 12..14 then 60
	   when 15..17 then 65
	   when 18..29 then 60
	   when 30..49 then 60
	   when 50..69 then 60
	   when 50..69 then 60
	   else 60
	end
   else
	case self.age
	   when 0 then 25
	   when 1..2 then 20
	   when 3..5 then 25
	   when 6..7 then 30
	   when 8..9 then 40
	   when 10..11 then 50
	   when 12..14 then 55
	   when 15..17 then 55
	   when 18..29 then 50
	   when 30..49 then 50
	   when 50..69 then 50
	   when 50..69 then 50
	   else 50
	end
   end

  end


end
