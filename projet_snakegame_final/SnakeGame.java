import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;


public class SnakeGame extends JPanel implements ActionListener, KeyListener{
    
    private class Tile{
        int x;
        int y;
        Tile(int x,int y){
            this.x=x;
            this.y=y;
        }
    }
    int boardWidth;
    int boardHeight;
    int tailleduncarreau=25;

    //pour le serpent 
    Tile têteduserpent;
    ArrayList<Tile> corpsduserpent;

    //pour la nourriture du serpent 
    Tile nourriture;
    Random random;

    // logique du jeu 
    Timer boucledujeu;
    int vélocitéX;
    int vélocitéY;
    Boolean gameOver= false; //pour les conditions du game over
    int score=0; // score du joueur
    int highscore=0; //highscore du jouer

    private BufferedImage appleImage;
    private BufferedImage trophyImage;
    int imageSize=30;
    

    // ---------------------------------------------------------------------------
    private BufferedImage backgroundTile; //Pour stocker l'image - Temi
    // ---------------------------------------------------------------------------


    SnakeGame (int boardWidth,int boardHeight){
        this.boardWidth=boardWidth;
        this.boardHeight=boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);


        têteduserpent =new Tile(5,5);//les x et les y sont compter en terme de case, chaque case fais 25 pixels, donc la tête du serpent est à 125 pixels vers la droite(pour les x) et 125 pixel vers le bas
        corpsduserpent=new ArrayList<Tile>();

        nourriture= new Tile(10,10);
        random=new Random();
        placeNourriture();//fonciton permettant de placer aléatoirement la nourriture sur le graphe 

        vélocitéX=0;
        vélocitéY=0;

        // ---------------------------------------------------------------------------
        // Charger l'image de fond - Temi
        try {
            backgroundTile = ImageIO.read(new File("tile.png"));
        }
        catch (Exception e){
            e.printStackTrace(); // affiche l'erreur
        }
        // ---------------------------------------------------------------------------

        boucledujeu= new Timer(60,this);
        boucledujeu.start();

        //-Samuel
    try{
        appleImage=ImageIO.read(new File("redapple.png"));
        trophyImage=ImageIO.read(new File("trophy.png"));
    } catch (Exception e){
        e.printStackTrace(); // affiche l'erreur
    }

    }


    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){

        // ---------------------------------------------------------------------------
        // Image de fond - Temi
        for (int x = 0; x < boardWidth/tailleduncarreau; x++){
            for (int y = 0; y < boardWidth/tailleduncarreau; y++)
            g.drawImage(backgroundTile, x*tailleduncarreau, y*tailleduncarreau, tailleduncarreau, tailleduncarreau, null); // null n'a pas d'importance ici
        }

        //nourriture: dessinde la nourriture sur le graphique 
        g.setColor(new Color(0xFF6B6B));


        int padding = 4; //Temi
        g.fillOval(nourriture.x*tailleduncarreau + padding,
                    nourriture.y*tailleduncarreau + padding
                    ,tailleduncarreau - 2*padding,
                    tailleduncarreau - 2*padding);

        // colorchoice - Temi
        Color CouleurTete = new Color(0x5C8F78);
        Color couleurCorps = new Color(0x6AA084);

        // ---------------------------------------------------------------------------

        //Tête du Serpent: dessin du serpent sur le grapique 
        g.setColor(CouleurTete);
        g.fillRect(têteduserpent.x*tailleduncarreau, têteduserpent.y*tailleduncarreau, tailleduncarreau, tailleduncarreau);//la multiplication permet de modifier l'emplacement de notre point vert
         
        //corps du serpent
        g.setColor(couleurCorps);
        for (int i=0; i<corpsduserpent.size(); i++){
            Tile partiduserpent= corpsduserpent.get(i);
            g.fillRect(partiduserpent.x*tailleduncarreau, partiduserpent.y*tailleduncarreau, tailleduncarreau, tailleduncarreau);
        }
        //Score and High Score - Sqmuel
        g.setColor(Color.black);
        g.setFont(new Font("Ink Free",Font.BOLD,40));
        //String scoreText="Score: "+ score;
        //String highscoreText= "High Score: "+ highscore;
        //FontMetrics metrics=g.getFontMetrics(g.getFont());
        //int x= (boardWidth-metrics.stringWidth(scoreText))/2;
        //int y=metrics.getHeight();

        //int highscorex=(boardWidth-metrics.stringWidth(highscoreText))/2;
       //int highscorey=metrics.getHeight();
        
        //g.drawString(scoreText,x,y);
        //g.drawString(highscoreText,highscorex,highscorey);

        String scoreText= String.valueOf(score);
        String highscoreText=String.valueOf(highscore);

        FontMetrics font=g.getFontMetrics(g.getFont());

        //Coordinates
        int spacing = 10;

        int x= boardWidth/2-100;
        int y=font.getHeight();

        int highscorex= x+ 100;
        int highscorey=y;

        g.drawImage(appleImage, x, y - imageSize, imageSize,imageSize,null);
        g.drawString(scoreText,x + imageSize+spacing,y);
        
        g.drawImage(trophyImage, highscorex, highscorey - imageSize, imageSize,imageSize,null);
        g.drawString(highscoreText,highscorex + imageSize+spacing,highscorey);
        




        // indication gameover - Temi
        if (gameOver) {
            g.setColor(new Color(0x2E8B7B));
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over!", boardWidth / 2 - 90, boardHeight / 2 - 20);
            g.drawString("Entrée ou Espace pour recommencer", boardWidth / 2 - 265, boardHeight / 2 + 20);
        }
    }
    public void placeNourriture(){
        nourriture.x=random.nextInt(boardWidth/tailleduncarreau);//valeur de  x qui est la position sur l'axe des abscisses compris entre [0,24]
        nourriture.y=random.nextInt(boardHeight/tailleduncarreau);//valeur de  y qui est la position sur l'axe des ordonnées compris entre [0,24], x est y sont pris aléatoirement entre 0 et 24
    }
    public boolean collision(Tile carreau1,Tile carreau2 ){
        return carreau1.x==carreau2.x&& carreau1.y==carreau2.y ;//pour la collision il vérifie la position de la tête du serpent et du point rouge
    }
    public void sedéplacer(){
        //pour manger de le point rouge
        if(collision(têteduserpent, nourriture)){
            corpsduserpent.add(new Tile(nourriture.x, nourriture.y));
            placeNourriture();

            score++; //pour on augment le score
            if (score>highscore){
                highscore=score;
            }
            duSon("eating.wav"); // quand la nourriture est mangé

        }

        //corps du serpent 
        for(int i=corpsduserpent.size()-1;i>=0;i--){
            Tile partiduserpent=corpsduserpent.get(i);
            if(i==0){
                partiduserpent.x=têteduserpent.x;
                partiduserpent.y=têteduserpent.y;
            }
            else{
                Tile prevpartiduserpent=corpsduserpent.get(i-1);
                partiduserpent.x=prevpartiduserpent.x;
                partiduserpent.y=prevpartiduserpent.y;
            }
        }
        
        //Snake head 
        têteduserpent.x +=vélocitéX;
        têteduserpent.y +=vélocitéY;

        //game over condition
        for(int i=0;i<corpsduserpent.size();i++){
            Tile partiduserpent = corpsduserpent.get(i);
            if (collision(têteduserpent,partiduserpent)){
                gameOver=true;
            }
        }
        if (têteduserpent.x<0){
           têteduserpent.x=24;
        }
        if(têteduserpent.x>24){
            têteduserpent.x=0;
        }
        if (têteduserpent.y<0){
            têteduserpent.y=24;
        }
        if (têteduserpent.y>24){
            têteduserpent.y=0;
        }

    }

    //Conditions du game over


    @Override
    public void actionPerformed(ActionEvent e) {
        sedéplacer();
        repaint();//va être appelé toutes les millisecondes par le programme va faire changer de couleur les différentes cases:vert->noir,noir->vert,vert->rouge,noir->rouge
        if (gameOver){
            duSon("game-over.wav");

            boucledujeu.stop();
            
        }
         }
   //à titre d'informatique sur le graphique l'extrimité en haut à gauche à pour coordonnées (0px,0px), et les coordonnées pour le cointoutes en bas à droite(600px,600px)

    @Override
    public void keyPressed(KeyEvent e) {

        // ---------------------------------------------------------------------------
        // Relancez le jeu si le jeu est terminée - Temi
        if (gameOver && (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)) {
            reInitialiser();
            return; // pour ne pas executer le reste du programme
        }
        // ---------------------------------------------------------------------------


        if (e.getKeyCode()==KeyEvent.VK_UP && vélocitéY!=1){//indique qu'on a appuyé sur la flêche du haut pour se déplacer, vas déplacer le serpentvert le haut, la touche fonctionne ssi le serpent ne va pas vers le bas 
        vélocitéX= 0;
        vélocitéY=-1;
        duSon("button.wav"); // pour les boutons
        }
        else if (e.getKeyCode()==KeyEvent.VK_DOWN && vélocitéY!=-1){//indique qu'on a appuyé sur la flêche du bas pour se déplacer vas déplacer le serpent le bas, la touche fonctionne ssi le serpent ne va pas vers le haut 
        vélocitéX= 0;
        vélocitéY=1;
        duSon("button.wav");
        }
        else if (e.getKeyCode()==KeyEvent.VK_LEFT && vélocitéX!=1){//indique qu'on a appuyé sur la flêche de gauche pour se déplacer vas déplacer le serpent le gauche, la touche fonctionne ssi le serpent ne va pas vers la droite
        vélocitéX= -1;
        vélocitéY=0;
        duSon("button.wav");
        }
        else if (e.getKeyCode()==KeyEvent.VK_RIGHT && vélocitéX!=-1){//indique qu'on a appuyé sur la flêche de droite pour se déplacer vas déplacer le serpent la droite, la touche fonctionne ssi le serpent ne va pas vers la gauche 
        vélocitéX= 1;
        vélocitéY=0;
        duSon("button.wav");
        }
    }

    // ---------------------------------------------------------------------------
    // Son Temi
    private void duSon(String chemin) {
        File file = new File(chemin);

        try(AudioInputStream audio = AudioSystem.getAudioInputStream(file)) {

            Clip clip = AudioSystem.getClip();
            clip.open(audio);

            clip.start();
        }
        catch(LineUnavailableException l) {
            System.out.println("Impossible d'accéder à la ressource audio");
        }
        catch(UnsupportedAudioFileException u) {
            System.out.println("Fichier audio non pris en charge");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    // reinitialiser - Temi & Samuel
    private void reInitialiser() {
        têteduserpent = new Tile(5,5);
        corpsduserpent.clear(); // corpsduserpent est une ArrayList qui stocke tous les segments du corps
        vélocitéX = 0;
        vélocitéY = 0;
        gameOver = false;
        score=0; //remettre à zero,  mais pas le high score 
        placeNourriture();
        boucledujeu.start();
        repaint(); // Force le panneau à se redessiner immédiatement en appelant paintComponent()
    }

    // ---------------------------------------------------------------------------

    //inutile
   @Override
    public void keyTyped(KeyEvent e) {}

    
    @Override
    public void keyReleased(KeyEvent e) {}

}