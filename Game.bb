;-------------------------------------------------------------
;/////////////////////// CONSOLE MAKER ///////////////////////
;-------------------------------------------------------------
;~~~ Programmed by  dual-core -- Published by Blitz Studio ~~~
;~~~~~~~~~~~~~~~~~~~~~~~ Version 1.0.2 ~~~~~~~~~~~~~~~~~~~~~~~

Graphics 1280,720,32
SetBuffer BackBuffer()
AppTitle "Console Maker"

;fonts
Dim font(3)
font(0)=LoadFont("fonts/arialbi.ttf",36)
font(1)=LoadFont("fonts/basis33.ttf",36)
font(2)=LoadFont("fonts/software_tester_7.ttf",36)

;loading screen
SetFont font(0)
Color 255,255,255
ClsColor 0,0,0

Cls
Text 640,360,"Loading assets...",True,True
Flip

;load graphics
Global gCursor=LoadImage("graphics/cursor.png")
Global gFail=LoadImage("graphics/failure.png")
Global gLogo=LoadImage("graphics/logo.png")
Global gSuccess=LoadImage("graphics/success.png")

;icons
Dim gIcon(11)
gIcon(0)=LoadImage("graphics/icons/action_icon.png")
gIcon(1)=LoadImage("graphics/icons/adventure_icon.png")
gIcon(2)=LoadImage("graphics/icons/cpu_icon.png")
gIcon(3)=LoadImage("graphics/icons/ff_icon.png")
gIcon(4)=LoadImage("graphics/icons/gpu_icon.png")
gIcon(5)=LoadImage("graphics/icons/handheld_icon.png")
gIcon(6)=LoadImage("graphics/icons/headset_icon.png")
gIcon(7)=LoadImage("graphics/icons/home_icon.png")
gIcon(8)=LoadImage("graphics/icons/mmo_icon.png")
gIcon(9)=LoadImage("graphics/icons/rpg_icon.png")
gIcon(10)=LoadImage("graphics/icons/simulation_icon.png")

;consoles
Dim gConsole(9)
gConsole(0)=LoadImage("graphics/consoles/handheld.png")
gConsole(1)=LoadImage("graphics/consoles/handheld_design.png")
gConsole(2)=LoadImage("graphics/consoles/handheld_partial.png")
gConsole(3)=LoadImage("graphics/consoles/headset.png")
gConsole(4)=LoadImage("graphics/consoles/headset_design.png")
gConsole(5)=LoadImage("graphics/consoles/headset_partial.png")
gConsole(6)=LoadImage("graphics/consoles/home.png")
gConsole(7)=LoadImage("graphics/consoles/home_design.png")
gConsole(8)=LoadImage("graphics/consoles/home_partial.png")

For count=0 To 8
 MaskImage gConsole(count),255,0,255
Next

;load sounds
Global sBad=LoadSound("sounds/bad_result.wav")
Global sGood=LoadSound("sounds/good_result.wav")
Global sPurchase=LoadSound("sounds/purchase.wav")

;load music
Global mMain=LoadSound("music/main.wav")
Global mMinigame=LoadSound("music/minigame.wav")

;tells the game to loop the sounds: does not actually play sounds
LoopSound mMain
LoopSound mMinigame

;setup channels
Global chMain=PlaySound(mMain) : StopChannel chMain
Global chMinigame=PlaySound(mMinigame) : StopChannel chMinigame

;game vars
Global stage=1
Global timer=CreateTimer(40)
Global filein, fileout
Global option
Global buildtim
Global contract_slide
Global pMoney
Global pRep
Global minigameOver
Dim alreadyPlayed(3) ;so the player can't replay minigames until they succeed.
;desired
Global dForm
Global dCPU
Global dGPU
Global dSDK
;achieved?
Global aForm
Global aCPU
Global aGPU
Global aSDK

;key names
Dim key$(9)
key$(0)="A"
key$(1)="S"
key$(2)="D"
key$(3)="F"
key$(4)="G"
key$(5)="H"
key$(6)="J"
key$(7)="K"
key$(8)="L"

;/////////////////////// 1. Titles ///////////////////////

Function Titles()
 If Not ChannelPlaying(chMain) Then chMain=PlaySound(mMain)
 logoX=0
 ClsColor 255,255,255
 Color 0,0,0
 SetFont font(1)
 fToggle=1
 Repeat
  If KeyHit(1) Then End
  If KeyHit(57) Then
   fileout=WriteFile("data/player.dat")
   WriteInt fileout,10000
   WriteInt fileout,20
   WriteInt fileout,1
   CloseFile fileout
   pMoney=10000
   pRep=20
   stage=2
  EndIf
  If KeyHit(28) Then
   filein=ReadFile("data/player.dat")
   pMoney=ReadInt(filein)
   pRep=ReadInt(filein)
   buildtim=ReadInt(filein)
   CloseFile filein : filein=OpenFile("data/contract.dat")
   dForm=ReadInt(filein)
   dCPU=ReadInt(filein)
   dGPU=ReadInt(filein)
   dSDK=ReadInt(filein)
   CloseFile filein
   stage=3
  EndIf
  WaitTimer(timer)
  Cls
  DrawImage gLogo,logoX,0
  If fToggle=1 Then Color 0,0,0
  If fToggle=0 Then Color 0,0,255
  Text 640,510,"PRESS SPACE FOR NEW GAME",True,True
  Text 640,580,"PRESS RETURN TO CONTINUE",True,True
  If fToggle=1 Then fToggle=0 : Goto endofloop ;blitz doesn't seem to have a Continue statement
  If fToggle=0 Then fToggle=1
  logoX=logoX+5
.endofloop
  Flip
 Until stage<>1
End Function

;/////////////////////// 2. Contract ///////////////////////

Function Contract()
 If Not ChannelPlaying(chMain) Then chMain=PlaySound(mMain)
 SeedRnd MilliSecs()
 dForm=Rnd(1,3)
 dCPU=Rnd(1,3)
 dGPU=Rnd(1,3)
 dSDK=Rnd(1,5)
 aForm=0
 aCPU=0
 aGPU=0
 aSDK=0
 ClsColor 255,255,255
 Color 0,0,0
 SetFont font(1)
 contract_slide=1
 Repeat
  If KeyHit(57) Then contract_slide=contract_slide+1
  WaitTimer(timer)
  Cls
  If contract_slide>=1 Then Text 640,100,"You have a new contract.",True,True
  If contract_slide>=2 Then
   If dForm=1 Then Text 640,170,"The client wants a handheld console...",True,True
   If dForm=2 Then Text 640,170,"The client wants a headset console...",True,True
   If dForm=3 Then Text 640,170,"The client wants a home console...",True,True
  EndIf
  If contract_slide>=3 Then
   If dCPU=1 Then Text 640,240,"...with a six-core processor...",True,True
   If dCPU=2 Then Text 640,240,"...with an eight-core processor...",True,True
   If dCPU=3 Then Text 640,240,"...with a ten-core processor...",True,True
  EndIf
  If contract_slide>=4 Then
   If dGPU=1 Then Text 640,310,"...a 500-core GPU...",True,True
   If dGPU=2 Then Text 640,310,"...a 700-core GPU...",True,True
   If dGPU=3 Then Text 640,310,"...a 1000-core GPU...",True,True
  EndIf
  If contract_slide>=5 Then
   Text 640,380,"...and an SDK tailored to the genre of...",True,True
   If dSDK=1 Then Text 640,450,"...action games.",True,True
   If dSDK=2 Then Text 640,450,"...adventure games.",True,True
   If dSDK=3 Then Text 640,450,"...MMO games.",True,True
   If dSDK=4 Then Text 640,450,"...role-playing games.",True,True
   If dSDK=5 Then Text 640,450,"...simulation games.",True,True
  EndIf
  If contract_slide>=6 Then
   fileout=WriteFile("data/contract.dat")
   WriteInt fileout,dForm
   WriteInt fileout,dCPU
   WriteInt fileout,dGPU
   WriteInt fileout,dSDK
   CloseFile fileout
   stage=3
  EndIf
  Flip
 Until stage<>2
End Function

;/////////////////////// 3. Menu ///////////////////////

Function Menu()
 If Not ChannelPlaying(chMain) Then chMain=PlaySound(mMain)
 ClsColor 255,255,255
 Color 0,0,0
 option=1
 SetFont font(1)
 If alreadyPlayed(0) And alreadyPlayed(1) And alreadyPlayed(2) Then
  stage=7
  Return
 EndIf
 Repeat
  If KeyHit(1) Then stage=1
  If KeyHit(200) And option>1 Then option=option-1
  If KeyHit(208) And option<3 Then option=option+1
  If KeyHit(28) Or KeyHit(57) Then
   If option=1 And alreadyPlayed(0)<>1 Then 
    alreadyPlayed(0)=1
    stage=4
   EndIf
   If option=2 And alreadyPlayed(1)<>1 Then
    alreadyPlayed(1)=1
    stage=5
   EndIf
   If option=3 And alreadyPlayed(2)<>1 Then
    alreadyPlayed(2)=1
    stage=6
   EndIf
  EndIf
  WaitTimer(timer)
  Cls
  If dForm=1 Then DrawImage gConsole(1),50,100
  If dForm=2 Then DrawImage gConsole(4),50,100
  If dForm=3 Then DrawImage gConsole(7),50,100
  If option=1 Then Color 0,0,255 Else Color 0,0,0
  Text 880,100,"Build Case",False,True
  If option=2 Then Color 0,0,255 Else Color 0,0,0
  Text 880,170,"Build Motherboard",False,True
  If option=3 Then Color 0,0,255 Else Color 0,0,0
  Text 880,240,"Design SDK",False,True
  Color 0,0,0
  Text 40,600,"Money: $"+Str pMoney,False,True
  Text 40,670,"Reputation: "+Str pRep,False,True
  Flip
 Until stage<>3
End Function

;/////////////////////// 4. Build Case Minigame ///////////////////////

Function BuildCase()
 If ChannelPlaying(chMain) Then StopChannel chMain
 If Not ChannelPlaying(chMinigame) Then chMinigame=PlaySound(mMinigame)
 SetFont font(2)
 ClsColor 0,128,128
 Color 255,255,255
 minigameOver=0
 time=MilliSecs()
 score=0
 desiredkey=Rnd(30,38)
 timeleft=20
 target=100
 playedresult=0 ;played result sound?
 Repeat
  If minigameOver And KeyHit(1) Then
   stage=3
  EndIf
  If Not minigameOver Then
   For count=30 To 38
    If desiredkey=count And KeyHit(count) Then
     score=score+5
     SeedRnd MilliSecs()
     desiredkey=Rnd(30,38)
    EndIf
   Next
   If MilliSecs()-time>=1000 Then
    time=MilliSecs()
    timeleft=timeleft-1
   EndIf
  EndIf
  WaitTimer(timer)
  Cls
   If dForm=1 Then DrawImage gConsole(0),390,110
   If dForm=2 Then DrawImage gConsole(3),390,110
   If dForm=3 Then DrawImage gConsole(6),390,110
   If Not minigameOver Then Text 640,360,"PRESS THE "+key$(desiredkey-30)+" KEY!",True,True
   If minigameOver Then Text 640,500,"PRESS ESCAPE TO CONTINUE",True,True
   Text 40,600,"Time Left: "+Str timeleft,False,True
   Text 40,670,"Score: "+Str score,False,True
   If timeleft<1 Then
    minigameOver=1
    DrawImage gFail,140,110 : aForm=0
    If Not playedresult Then
     PlaySound(sBad)
     playedresult=1
    EndIf
   EndIf
   If score>=target Then
    minigameOver=1
    DrawImage gSuccess,140,110 : aForm=1
    If Not playedresult Then
     PlaySound(sGood)
     playedresult=1
    EndIf
   EndIf
  Flip
 Until stage<>4
 StopChannel chMinigame
End Function

;/////////////////////// 5. Build Motherboard Minigame ///////////////////////

;it's okay to copy and paste if it's your own code, right?
Function BuildMotherboard()
 If ChannelPlaying(chMain) Then StopChannel chMain
 If Not ChannelPlaying(chMinigame) Then chMinigame=PlaySound(mMinigame)
 SetFont font(2)
 ClsColor 0,128,128
 Color 255,255,255
 minigameOver=0
 time=MilliSecs()
 score=0
 desiredkey=Rnd(30,38)
 timeleft=20
 target=100
 playedresult=0 ;played result sound?
 Repeat
  If minigameOver And KeyHit(1) Then
   stage=3
  EndIf
  If Not minigameOver Then
   For count=30 To 38
    If desiredkey=count And KeyHit(count) Then
     score=score+5
     SeedRnd MilliSecs()
     desiredkey=Rnd(30,38)
    EndIf
   Next
   If MilliSecs()-time>=1000 Then
    time=MilliSecs()
    timeleft=timeleft-1
   EndIf
  EndIf
  WaitTimer(timer)
  Cls
   If dForm=1 Then DrawImage gConsole(2),390,110
   If dForm=2 Then DrawImage gConsole(5),390,110
   If dForm=3 Then DrawImage gConsole(8),390,110
   If Not minigameOver Then Text 640,360,"PRESS THE "+key$(desiredkey-30)+" KEY!",True,True
   If minigameOver Then Text 640,500,"PRESS ESCAPE TO CONTINUE",True,True
   Text 40,600,"Time Left: "+Str timeleft,False,True
   Text 40,670,"Score: "+Str score,False,True
   If timeleft<1 Then
    minigameOver=1
    DrawImage gFail,140,110 : aGPU=0 : aCPU=0
    If Not playedresult Then
     PlaySound(sBad)
     playedresult=1
    EndIf
   EndIf
   If score>=target Then
    minigameOver=1
    DrawImage gSuccess,140,110 : aGPU=1 : aCPU=1
    If Not playedresult Then
     PlaySound(sGood)
     playedresult=1
    EndIf
   EndIf
  Flip
 Until stage<>5
 StopChannel chMinigame
End Function

;/////////////////////// 6. Design SDK Minigame ///////////////////////

;yeah... it's probably okay.
Function DesignSDK()
 If ChannelPlaying(chMain) Then StopChannel chMain
 If Not ChannelPlaying(chMinigame) Then chMinigame=PlaySound(mMinigame)
 SetFont font(2)
 ClsColor 0,128,128
 Color 192,192,192
 minigameOver=0
 time=MilliSecs()
 score=0
 desiredkey=Rnd(30,38)
 timeleft=20
 target=100
 playedresult=0 ;played result sound?
 Repeat
  If minigameOver And KeyHit(1) Then
   stage=3
  EndIf
  If Not minigameOver Then
   For count=30 To 38
    If desiredkey=count And KeyHit(count) Then
     score=score+5
     SeedRnd MilliSecs()
     desiredkey=Rnd(30,38)
    EndIf
   Next
   If MilliSecs()-time>=1000 Then
    time=MilliSecs()
    timeleft=timeleft-1
   EndIf
  EndIf
  WaitTimer(timer)
  Cls
   Select True
    Case dSDK=1
     DrawImage gIcon(0),390,110
    Case dSDK=2
     DrawImage gIcon(1),390,110
    Case dSDK=3
     DrawImage gIcon(8),390,110
    Case dSDK=4
     DrawImage gIcon(9),390,110
    Case dSDK=5
     DrawImage gIcon(10),390,110
   End Select
   If Not minigameOver Then Text 640,360,"PRESS THE "+key$(desiredkey-30)+" KEY!",True,True
   If minigameOver Then Text 640,500,"PRESS ESCAPE TO CONTINUE",True,True
   Text 40,600,"Time Left: "+Str timeleft,False,True
   Text 40,670,"Score: "+Str score,False,True
   If timeleft<1 Then
    minigameOver=1
    DrawImage gFail,140,110 : aSDK=0
    If Not playedresult Then
     PlaySound(sBad)
     playedresult=1
    EndIf
   EndIf
   If score>=target Then
    minigameOver=1
    DrawImage gSuccess,140,110 : aSDK=1
    If Not playedresult Then
     PlaySound(sGood)
     playedresult=1
    EndIf
   EndIf
  Flip
 Until stage<>6
 StopChannel chMinigame
End Function

;/////////////////////// 7. Results ///////////////////////

Function Results()
 If Not ChannelPlaying(chMain) Then chMain=PlaySound(mMain)
 SetFont font(0)
 ClsColor 255,255,255
 Color 0,0,0
 ;calculate how much money the player spent and earned
 cCase=dForm*1000
 cCPU=dCPU*300
 cGPU=dGPU*500
 cSDK=dSDK*100
 tCost=cCase+cCPU+cGPU+cSDK
 If aForm=1 Then eCase=dForm*1500 Else eCase=0
 If aCPU=1 Then eCPU=dCPU*450 Else eCPU=0
 If aGPU=1 Then eGPU=dGPU*750 Else eGPU=0
 If aSDK=1 Then eSDK=dSDK*150 Else eSDK=0
 tEarn=eCase+eCPU+eGPU+eSDK
 Profit=tEarn-tCost
 pMoney=pMoney+Profit
 ;how many did they get right?
 num_right=aForm+aCPU+aGPU+aSDK
 If num_right>3 Then pRep=pRep+5 ;reputation bonus
 If num_right<2 Then pRep=pRep-5 ;reputation penalty
 result_slide=0
 time=MilliSecs()
 ;reset the already played values or we'll be stuck at this screen
 For count=0 To 2
  alreadyPlayed(count)=0
 Next
 Repeat
  If result_slide>=5 And KeyHit(1) Then
   If pMoney<1 Or pRep<1 Then
    fileout=WriteFile("data/player.dat")
    WriteInt fileout,10000
    WriteInt fileout,20
    WriteInt fileout,1
    CloseFile fileout
    stage=8
   ElseIf pRep>=100 Then
    fileout=WriteFile("data/player.dat")
    WriteInt fileout,10000
    WriteInt fileout,20
    WriteInt fileout,1
    CloseFile fileout
    stage=9
   Else
    fileout=WriteFile("data/player.dat")
    WriteInt fileout,pMoney
    WriteInt fileout,pRep
    WriteInt fileout,1
    CloseFile fileout
    stage=2
   EndIf
  EndIf
  If MilliSecs()-time>=500 Then
   result_slide=result_slide+1
   time=MilliSecs()
  EndIf
  WaitTimer(timer)
  Cls
   If result_slide>=0 Then Text 640,200,"CONTRACT COMPLETE!",True,True
   If result_slide>=1 Then Text 640,300,"Development Costs: $"+Str tCost,True,True
   If result_slide>=2 Then Text 640,370,"Earnings From Client: $"+Str tEarn,True,True
   If result_slide>=3 Then
    If Profit<0 Then
     Color 128,0,0
     Text 640,440,"Deficit: $"+Str Abs(Profit),True,True
    Else
     Color 0,128,0
     Text 640,440,"Profit: $"+Str Abs(Profit),True,True
    EndIf
    Color 0,0,0
   EndIf
   If result_slide>=4 Then
    If num_right>3 Then
     Color 0,0,128
     Text 640,510,"Reputation Bonus +5 Points",True,True
    ElseIf num_right<2 Then
     Color 128,0,0
     Text 640,510,"Reputation Penalty -5 Points",True,True
    EndIf
    Color 0,0,0
   EndIf
   If result_slide>=5 Then
    Text 640,580,"Press Escape to Continue...",True,True
   EndIf
  Flip
 Until stage<>7
End Function

;/////////////////////// 8. Game Over ///////////////////////

Function GameOver()
 Color 255,0,0
 ClsColor 0,0,0
 If Not ChannelPlaying(chMain) Then chMain=PlaySound(mMain)
 Repeat
  If KeyHit(1) Then stage=1
  Cls
  SetFont font(2)
  Text 640,300,"GAME OVER",True,True
  SetFont font(1)
  Text 640,400,"Game Design, Graphics, and Programming - Blitz Studio",True,True
  Text 640,470,"Main Menu Music: 'Powerful Trap Beat' by Alex-Productions",True,True
  Text 640,540,"Minigame Music: 'Space Game' by Francisco Alvear",True,True
  Text 640,610,"Better luck next time!",True,True
  Flip
 Until stage<>8
End Function

;/////////////////////// 9. You Won ///////////////////////

Function YouWon()
 Color 0,255,0
 ClsColor 0,0,0
 If Not ChannelPlaying(chMain) Then chMain=PlaySound(mMain)
 Repeat
  If KeyHit(1) Then stage=1
  Cls
  SetFont font(2)
  Text 640,300,"YOU WON!",True,True
  SetFont font(1)
  Text 640,400,"Game Design, Graphics, and Programming - Blitz Studio",True,True
  Text 640,470,"Main Menu Music: 'Powerful Trap Beat' by Alex-Productions",True,True
  Text 640,540,"Minigame Music: 'Space Game' by Francisco Alvear",True,True
  Text 640,610,"Thanks for playing!",True,True
  Flip
 Until stage<>9
End Function

;/////////////////////// MAIN LOOP ///////////////////////

While stage>0
 If stage=1 Then Titles()
 If stage=2 Then Contract()
 If stage=3 Then Menu()
 If stage=4 Then BuildCase()
 If stage=5 Then BuildMotherboard()
 If stage=6 Then DesignSDK()
 If stage=7 Then Results()
 If stage=8 Then GameOver()
 If stage=9 Then YouWon()
Wend
