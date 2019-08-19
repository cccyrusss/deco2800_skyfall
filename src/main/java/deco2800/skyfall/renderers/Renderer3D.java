package deco2800.skyfall.renderers;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import deco2800.skyfall.animation.AnimationLinker;
import deco2800.skyfall.animation.AnimationRole;
import deco2800.skyfall.entities.*;
import deco2800.skyfall.managers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import deco2800.skyfall.tasks.AbstractTask;
import deco2800.skyfall.tasks.MovementTask;
import deco2800.skyfall.util.HexVector;
import deco2800.skyfall.util.Vector2;
import deco2800.skyfall.util.WorldUtil;
import deco2800.skyfall.worlds.Tile;

/**
 * A ~simple~ complex hex renderer for DECO2800 games
 * 
 * @Author Tim Hadwen & Lachlan Healey
 */
public class Renderer3D implements Renderer {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(Renderer3D.class);

	BitmapFont font;
	
	//mouse cursor
	private static final String TEXTURE_SELECTION = "selection";
	private static final String TEXTURE_DESTINATION = "selection";
	private static final String TEXTURE_PATH = "path";
	private float elapsedTime=0f;
	private int tilesSkipped = 0;

	private TextureManager textureManager = GameManager.getManagerFromInstance(TextureManager.class);
    private AnimationManager animationManager = GameManager.getManagerFromInstance(AnimationManager.class);

	/**
	 * Renders onto a batch, given a renderables with entities It is expected
	 * that AbstractWorld contains some entities and a Map to read tiles from
	 * 
	 * @param batch
	 *            Batch to render onto
	 */
	@Override
	public void render(SpriteBatch batch, OrthographicCamera camera) {
		if (font == null) {
			font = new BitmapFont();
			font.getData().setScale(1f);
		}

		// Render tiles onto the map
		List<Tile> tileMap = GameManager.get().getWorld().getTileMap();
		List<Tile> tilesToBeSkipped = new ArrayList<>();
        elapsedTime += Gdx.graphics.getDeltaTime();

        batch.begin();
		// Render elements section by section
		//	tiles will render the static entity attaced to each tile after the tile is rendered

		tilesSkipped = 0;
		for (Tile t: tileMap) {
			// Render each tile
			renderTile(batch, camera, tileMap, tilesToBeSkipped, t);

			// Render each undiscovered area
		}

		renderAbstractEntities(batch, camera);

		renderMouse(batch);

		debugRender(batch, camera);

		batch.end();
	}

	/**
	 *	Render an animation
	 * @param batch the sprite batch.
	 * @param camera the camera.
	 * @param animation the animation need to rend
	 * @param x animation x coordinate
	 * @param y	animation y coordinate
	 */
	private void renderAnimation(SpriteBatch batch, OrthographicCamera camera, Animation<TextureRegion> animation,Float x,Float y){
		elapsedTime+= Gdx.graphics.getDeltaTime();
		batch.draw(animation.getKeyFrame(elapsedTime,true),x,y,
				animation.getKeyFrame(elapsedTime,true).getRegionWidth()* WorldUtil.SCALE_X,
				animation.getKeyFrame(elapsedTime,true).getRegionHeight()* WorldUtil.SCALE_Y );
	}

	/**
	 * Render a single tile.
	 * @param batch the sprite batch.
	 * @param camera the camera.
	 * @param tileMap the tile map.
	 * @param tilesToBeSkipped a list of tiles to skip.
	 * @param tile the tile to render.
	 */
	private void renderTile(SpriteBatch batch, OrthographicCamera camera, List<Tile> tileMap, List<Tile> tilesToBeSkipped, Tile tile) {

        if (tilesToBeSkipped.contains(tile)) {
            return;
        }
        float[] tileWorldCord = WorldUtil.colRowToWorldCords(tile.getCol(), tile.getRow());

        if (WorldUtil.areCoordinatesOffScreen(tileWorldCord[0], tileWorldCord[1], camera)) {
            tilesSkipped++;
            GameManager.get().setTilesRendered(tileMap.size() - tilesSkipped);
            GameManager.get().setTilesCount(tileMap.size());
            return;
        }

        Texture tex = tile.getTexture();
			batch.draw(tex, tileWorldCord[0], tileWorldCord[1], tex.getWidth() * WorldUtil.SCALE_X,
					tex.getHeight() * WorldUtil.SCALE_Y);
        if (GameManager.getPaused()) {
            Color c = batch.getColor();
            batch.setColor(c.r, c.g, c.b, .5f);
        }
		GameManager.get().setTilesRendered(tileMap.size() - tilesSkipped);
		GameManager.get().setTilesCount(tileMap.size());
		

	}


	

	/**
	 * Render the tile under the mouse.
	 * @param batch the sprite batch.
	 */
    private void renderMouse(SpriteBatch batch) {
        Vector2 mousePosition = GameManager.getManagerFromInstance(InputManager.class).getMousePosition();

        Texture tex = textureManager.getTexture(TEXTURE_SELECTION);

        // get mouse position
        float[] worldCoord = WorldUtil.screenToWorldCoordinates(mousePosition.getX(), mousePosition.getY());

        // snap to the tile under the mouse by converting mouse position to colrow then back to mouse coordinates
        float[] colrow = WorldUtil.worldCoordinatesToColRow(worldCoord[0], worldCoord[1]);

        float[] snapWorldCoord = WorldUtil.colRowToWorldCords(colrow[0], colrow[1] + 1);

        //Needs to getTile with a HexVector for networking to work atm
        Tile tile = GameManager.get().getWorld().getTile(new HexVector(colrow[0], colrow[1]));

        if (tile != null) {
            batch.draw(tex, (int) snapWorldCoord[0], (int) snapWorldCoord[1]  - (tex.getHeight() * WorldUtil.SCALE_Y), 
                    tex.getWidth() * WorldUtil.SCALE_X,
                    tex.getHeight() * WorldUtil.SCALE_Y);
        }

	}

	/**
	 *  Robot animation
	 * @param batch SpriteBatch
	 * @param camera camera
	 * @param entity entity
	 * @param playerPeon Player peon
	 */
	private void robotAnimation(SpriteBatch batch, OrthographicCamera camera,AbstractEntity entity,MainCharacter playerPeon){
		//if the distance between player peon and Robot is greater than 2 then Robot do defence animation
		//(distance^2=(Robot col -Robot col)^2 +(Robot row -Robot row))
		if(entity instanceof Robot && playerPeon!=null){
			Robot robot=(Robot) entity;
			float colDistance=playerPeon.getCol()-robot.getCol();
			float rowDistance=playerPeon.getRow()-robot.getRow();
			if((colDistance*colDistance+rowDistance*rowDistance)<4){
				float[] tileWorldCord = WorldUtil.colRowToWorldCords(robot.getCol(), robot.getRow());
				renderAnimation(batch,camera,animationManager.getAnimation("robot_defence"),tileWorldCord[0],tileWorldCord[1]);
			}else{
				Texture savageTexture = textureManager.getTexture(robot.getTexture());
				float[] savageCoord = WorldUtil.colRowToWorldCords(robot.getCol(), robot.getRow());
				renderAbstractEntity(batch, robot, savageCoord, savageTexture);
			}


		}
	}

	/**
	 * skip draw enemy entities
	 * @param batch the spriteBatch
	 * @param entity AbstractEntity
	 * @param entityWorldCoord coordinate of entity world
	 * @param tex texture of entity
	 */
	private void skipDrawingEnemy(SpriteBatch batch,AbstractEntity entity,float[] entityWorldCoord,Texture tex){
		//if the entity is spider of savage then not drawing
		if(entity instanceof Spider || entity instanceof Robot){

		}else{
			renderAbstractEntity(batch, entity, entityWorldCoord, tex);
		}
	}


	/**
	 * Spider defence Animation
	 * @param batch the sprite batch
	 * @param camera the camera.
	 * @param entity AbstractEntity
	 * @param playerPeon playerPeon
	 */
	private void spiderAnimation(SpriteBatch batch, OrthographicCamera camera,AbstractEntity entity,MainCharacter playerPeon){

		//if the distance between player peon and spider is greater than 2 then spider do defence animation
		//(distance^2=(player col -spider col)^2 +(spider row -spider row))
		if (entity instanceof Spider && playerPeon!=null){
			Spider spider=(Spider) entity;
			float colDistance=playerPeon.getCol()-spider.getCol();
			float rowDistance=playerPeon.getRow()-spider.getRow();
			if((colDistance*colDistance+rowDistance*rowDistance)<4){
				float[] tileWorldCord = WorldUtil.colRowToWorldCords(spider.getCol(), spider.getRow());

				renderAnimation(batch,camera,animationManager.getAnimation("spider_defence"),tileWorldCord[0],tileWorldCord[1]);
			}else{
				//draw spider texture when distance greater than 2
				Texture spiderTexture = textureManager.getTexture(spider.getTexture());
				float[] spiderCoord = WorldUtil.colRowToWorldCords(spider.getCol(), spider.getRow());
				renderAbstractEntity(batch, spider, spiderCoord, spiderTexture);
			}
		}

	}

	/**
	 * To find playerPeon
	 * @param entities	AbstractEntity
	 * @return entity playerPeon
	 */
	private MainCharacter findPlayerPeon(List<AbstractEntity> entities){
		//find playerPeon in the entities list
		MainCharacter mainCharacter=null;
		//iterate abstract entity to find Player peon
		for(AbstractEntity e: entities){
			if(e instanceof MainCharacter){
				mainCharacter=(MainCharacter) e;
			}
		}
		return  mainCharacter;
	}
    /**
	 * Render all the entities on in view, including movement tiles, and excluding undiscovered area.
	 * @param batch the sprite batch.
	 * @param camera the camera.
	 */
	private void renderAbstractEntities(SpriteBatch batch, OrthographicCamera camera) {
		List<AbstractEntity> entities = GameManager.get().getWorld().getSortedEntities();
		MainCharacter playerPeon=findPlayerPeon(entities);

		int entitiesSkipped = 0;
		logger.debug("NUMBER OF ENTITIES IN ENTITY RENDER LIST: {}", entities.size());
		for (AbstractEntity entity : entities) {
			Texture tex = textureManager.getTexture(entity.getTexture());
			float[] entityWorldCoord = WorldUtil.colRowToWorldCords(entity.getCol(), entity.getRow());
			// If it's offscreen
			if (WorldUtil.areCoordinatesOffScreen(entityWorldCoord[0], entityWorldCoord[1], camera)) {
				entitiesSkipped++;
				continue;
			}
			skipDrawingEnemy(batch,entity,entityWorldCoord,tex);

            AnimationRole moveType = entity.getMovingAnimation();

            if (moveType == AnimationRole.NULL) {
				skipDrawingEnemy(batch, entity, entityWorldCoord, tex);
			} else {
                runMovementAnimations(batch, entity, entityWorldCoord, tex);
            }



			/* Draw Peon */
			// Place movement tiles
			if (entity instanceof Peon && GameManager.get().showPath) {
				renderPeonMovementTiles(batch, camera, entity, entityWorldCoord);
			 }
			spiderAnimation(batch,camera,entity,playerPeon);
			robotAnimation(batch,camera,entity,playerPeon);
			
			if (entity instanceof StaticEntity) {
			    StaticEntity staticEntity = ((StaticEntity) entity);
				Set<HexVector> childrenPosns = staticEntity.getChildrenPositions();
				for(HexVector childpos: childrenPosns) {
					Texture childTex = staticEntity.getTexture(childpos);
					
					float[] childWorldCoord = WorldUtil.colRowToWorldCords(childpos.getCol(), childpos.getRow());
										
					// time for some funky math: we want to render the entity at the centre of the tile. 
					// this way centres of textures bigger than tile textures render exactly on the top of the tile centre
					// think of a massive tree with the tree trunk at the centre of the tile 
					// and it's branches and leaves over surrounding tiles 
					
					// We get the tile height and width :
					int w = GameManager.get().getWorld().getTile(childpos).getTexture().getWidth();
					int h = GameManager.get().getWorld().getTile(childpos).getTexture().getHeight(); 
					
					int drawX = (int) (childWorldCoord[0] + (w - childTex.getWidth()) /2 * WorldUtil.SCALE_X);
					int drawY = (int) (childWorldCoord[1] + (h - childTex.getHeight())/2 * WorldUtil.SCALE_Y);

					batch.draw(
							childTex, drawX, drawY, 
							childTex.getWidth() * WorldUtil.SCALE_X, 
							childTex.getHeight() * WorldUtil.SCALE_Y );				 
				}
			}
            runAnimations(batch, entity, entityWorldCoord);
		}

		GameManager.get().setEntitiesRendered(entities.size() - entitiesSkipped);
		GameManager.get().setEntitiesCount(entities.size());
	}

	private void renderAbstractEntity(SpriteBatch batch, AbstractEntity entity, float[] entityWorldCord, Texture tex) {
        float x = entityWorldCord[0];
		float y = entityWorldCord[1];

        float width = tex.getWidth() * entity.getColRenderLength() * WorldUtil.SCALE_X;
        float height = tex.getHeight() * entity.getRowRenderLength() * WorldUtil.SCALE_Y;
        batch.draw(tex, x, y, width, height);
    }
	
	private void renderPeonMovementTiles(SpriteBatch batch, OrthographicCamera camera, AbstractEntity entity, float[] entityWorldCord) {
		Peon actor = (Peon) entity;
		AbstractTask task = actor.getTask();
		if (task instanceof MovementTask) {
			if (((MovementTask)task).getPath() == null) { //related to issue #8
				return;
			}
			List<Tile> path = ((MovementTask)task).getPath();
			for (Tile tile : path) {
				// Place transparent tiles for the path, but place a non-transparent tile for the destination
				Texture tex = path.get(path.size() - 1) == tile ?
						textureManager.getTexture(TEXTURE_DESTINATION) : textureManager.getTexture(TEXTURE_PATH);
				float[] tileWorldCord = WorldUtil.colRowToWorldCords(tile.getCol(), tile.getRow());
				if (WorldUtil.areCoordinatesOffScreen(tileWorldCord[0], tileWorldCord[1], camera)) {
					tilesSkipped++;
					continue;
				}
				batch.draw(tex, tileWorldCord[0],
						tileWorldCord[1]// + ((tile.getElevation() + 1) * elevationZeroThiccness * WorldUtil.SCALE_Y)
						, tex.getWidth() * WorldUtil.SCALE_X,
						tex.getHeight() * WorldUtil.SCALE_Y);

			}
//			if (!path.isEmpty()) {
//				// draw Peon
//				Texture tex = textureManager.getTexture(entity.getTexture());
//				batch.draw(tex, entityWorldCord[0], entityWorldCord[1] + entity.getHeight(),// + path.get(0).getElevation()) * elevationZeroThiccness * WorldUtil.SCALE_Y,
//						tex.getWidth() * entity.getColRenderLength() * WorldUtil.SCALE_X,
//						tex.getHeight() * entity.getRowRenderLength() * WorldUtil.SCALE_Y);
//			}
		}
	}
	
	private void debugRender(SpriteBatch batch, OrthographicCamera camera) {

		if (GameManager.get().showCoords) {
			List<Tile> tileMap = GameManager.get().getWorld().getTileMap();
			for (Tile tile : tileMap) {
				float[] tileWorldCord = WorldUtil.colRowToWorldCords(tile.getCol(), tile.getRow());

				if (!WorldUtil.areCoordinatesOffScreen(tileWorldCord[0], tileWorldCord[1], camera)) {
					font.draw(batch, 
							tile.toString(),
							//String.format("%.0f, %.0f, %d",tileWorldCord[0], tileWorldCord[1], tileMap.indexOf(tile)),
							tileWorldCord[0] + WorldUtil.TILE_WIDTH / 4.5f,
							tileWorldCord[1]);// + ((tile.getElevation() + 1) * elevationZeroThiccness * WorldUtil.SCALE_Y)
							//+ WorldUtil.TILE_HEIGHT-10);			
				}

			}
		}

		if (GameManager.get().showCoordsEntity) {
			List<AbstractEntity> entities = GameManager.get().getWorld().getEntities();
			for (AbstractEntity entity : entities) {
				float[] tileWorldCord = WorldUtil.colRowToWorldCords(entity.getCol(), entity.getRow());

				if (!WorldUtil.areCoordinatesOffScreen(tileWorldCord[0], tileWorldCord[1], camera)) {
					font.draw(batch, String.format("%.0f, %.0f", entity.getCol(), entity.getRow()),
							tileWorldCord[0], tileWorldCord[1]);
				}
			}
		}
	}


    /**
     * Runs the movement animations for the current entity. If NULL draws the
     * static texture gotten by Entity.getTexture().
     * @param batch Sprite batch to draw onto
     * @param entity Entity who the animation is associate with
     * @param entityWorldCoord Where on the game screen the entity is
     * @param tex Texture to draw if animation does not exist or is in state
	 *            AnimationRole.NULL
     */
	private void runMovementAnimations(SpriteBatch batch, AbstractEntity entity, float[] entityWorldCoord, Texture tex) {
        AnimationRole moveType = entity.getMovingAnimation();
        String animationName = entity.getAnimationName(moveType);

        if (animationName == null) {
            System.out.println("Could not find animation in entity" + entity.getObjectName());
            renderAbstractEntity(batch, entity, entityWorldCoord, tex);
        } else {
            Animation<TextureRegion> runAnimation = animationManager.getAnimation(animationName);

            if (runAnimation == null) {
                System.out.println("Could not find animation object in animationManager");

                renderAbstractEntity(batch, entity, entityWorldCoord, tex);
            } else {
                TextureRegion frame = runAnimation.getKeyFrame(elapsedTime, true);
                float width = frame.getRegionWidth() * entity.getColRenderLength() * WorldUtil.SCALE_X;
                float height = frame.getRegionHeight() * entity.getRowRenderLength() * WorldUtil.SCALE_Y;
                batch.draw(frame, entityWorldCoord[0] ,entityWorldCoord[1], width, height);
            }
        }
    }



    /**
     * Runs all other non-looping animations for the entity
     * @param batch Sprite batch to draw onto
     * @param entity Current entity
     * @param entityWorldCoord Where to draw.
     */
	private void runAnimations(SpriteBatch batch, AbstractEntity entity, float[] entityWorldCoord){
        Queue<AnimationLinker> q = entity.getToBeRun();
        int queueSize = q.size();
		if (queueSize == 0) {
		    return;
        }

		for (int i = 0; i < queueSize ; i++) {
            AnimationLinker aniLink = q.remove();
            Animation<TextureRegion> ani = aniLink.getAnimation();
            float time = aniLink.getStartingTime();

            if (ani == null) {
                System.out.println("Animation is null");
                continue;
            }

            if (ani.isAnimationFinished(time)) {
                System.out.println("Animation is done");
                continue;
            }

            TextureRegion currentFrame = ani.getKeyFrame(time, false);
            float width = currentFrame.getRegionWidth() * entity.getColRenderLength() * WorldUtil.SCALE_X;
			float height = currentFrame.getRegionHeight() * entity.getRowRenderLength() * WorldUtil.SCALE_Y;
			int[] offset = aniLink.getOffset();


            batch.draw(currentFrame ,entityWorldCoord[0]  + offset[0] ,entityWorldCoord[1] + offset[0], width,  height);
            aniLink.incrTime(Gdx.graphics.getDeltaTime());
            q.add(aniLink);
        }
	}
}
