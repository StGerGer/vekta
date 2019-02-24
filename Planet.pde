 /**
*  Model for a planet.
*/
class Planet implements SpaceObject {
  // Default Planet settings
  private final double DEF_MASS = 1.989 * Math.pow(10, 30);
  private final int DEF_RADIUS = 15;
  private final PVector DEF_POSITION = new PVector(100, 100);
  private final PVector DEF_VELOCITY = new PVector(0,0);
  private final color DEF_COLOR = color(255, 255, 255);
  private final float SPLIT_MASS_DECAY = .2;
  private final double MIN_SPLIT_RADIUS = 6;
  private final float SPLIT_DISTANCE_SCALE = 3;
  private final float SPLIT_VELOCITY_SCALE = 5;
  private final float MAX_INFLUENCE_ACCEL = 10;
  private String[] nameParts1 = loadStrings("data/text/planet_prefixes.txt"); // Planet name prefixes
  private String[] nameParts2 = concat(loadStrings("data/text/planet_suffixes.txt"), new String[] {""}); // Planet name suffixes
  
  private String name;
  private int id;
  private double mass;
  private float radius;
  private PVector position;
  private PVector velocity;
  private color c;

  /**
  *  Default constructor for planets
  */
  public Planet() {
    mass     = DEF_MASS;
    radius   = DEF_RADIUS;
    position = DEF_POSITION;
    velocity = DEF_VELOCITY;
    c = DEF_COLOR;
  }
  
  public Planet(int id, double mass, float radius, int x, int y, float xVelocity, float yVelocity, color c) {
    this(id, mass, radius, new PVector(x, y), new PVector(xVelocity, yVelocity), c);
  }
  
  public Planet(int id, double mass, float radius, PVector position, PVector velocity, color c) {
    
    this.name = nameParts1[(int)random(nameParts1.length)] + nameParts2[(int)random(nameParts2.length)];
    this.id = id;
    this.mass = mass;
    this.radius = radius;
    this.position = position;
    this.velocity = velocity;
    this.c = c;
  }
  
  // Draws the planet
  void draw() {
    stroke(this.c);
    fill(0);
    ellipseMode(RADIUS);
    if(id == 1) System.out.println(velocity);
    ellipse(position.x, position.y, radius, radius);
  }
  
  void update() {
    position.add(velocity);
  }  
  
  // TODO: move common SpaceObject logic to default interface methods or abstract parent class
  /**
    Calculates influence of this Planet *from* another object in space.
  */
  PVector applyInfluenceVector(ArrayList<SpaceObject> space) {
    for(int i = 0; i < space.size(); i++) {
      PVector influence = new PVector(0, 0);
      SpaceObject s = space.get(i);
      float dist = position.dist(s.getPosition());
      if(dist < MAX_DISTANCE) {
        double r = dist * SCALE;
        if(r == 0) return new PVector(0,0); // If the planet being checked is itself (or directly on top), don't move
        double force = G * ((mass * s.getMass()) / (r * r)); // G defined in orbit
        influence.add(new PVector(s.getPosition().x - position.x, s.getPosition().y - position.y).setMag((float)(force / mass)));
      }
      // Prevent insane acceleration
      influence.limit(MAX_INFLUENCE_ACCEL);
      velocity.add(influence);
    }
    // TODO: is this supposed to be `return velocity;`?
    return new PVector();
  }
  
  void onDestroy(SpaceObject s) {
    println("Planet destroyed with radius: " + getRadius());
    
    // Add this object's mass and radius to the mass and radius of the destroying object
    if(!(s instanceof Projectile)) {
      s.setMass(s.getMass() + mass * SPLIT_MASS_DECAY);
      s.setRadius(sqrt(s.getRadius() * s.getRadius() + radius * radius * SPLIT_MASS_DECAY * SPLIT_MASS_DECAY));
    } else {
      if(getRadius() >= MIN_SPLIT_RADIUS) {
        // Split large planet
        double newMass = getMass() / 2;
        float newRadius = getRadius() / sqrt(2);
        PVector offset = PVector.random2D().normalize().mult(newRadius * SPLIT_DISTANCE_SCALE);
        PVector splitVelocity = /*PVector.random2D()*/getPosition().copy().sub(s.getPosition()).rotate(90).normalize().mult(SPLIT_VELOCITY_SCALE);
        // Note that the planet ids are overwritten by addObject(..) logic
        // TODO: remove id parameter from constructor and allow addObject(..) to configure this automatically
        Planet a = new Planet(id, newMass, newRadius, getPosition().copy().add(offset), getVelocity().copy().add(splitVelocity), getColor());
        Planet b = new Planet(id, newMass, newRadius, getPosition().copy().sub(offset), getVelocity().copy().sub(splitVelocity), getColor());
        if(!s.collidesWith(a)) {
          addObject(a);
        }
        if(!s.collidesWith(b)) {
          addObject(b);
        }
      }
    }
  }  
  
  /**
    Checks if this planet collides with another planet
  */
  boolean collidesWith(SpaceObject s) {
    double r = PVector.dist(position, s.getPosition());
    if(r < (getRadius() + s.getRadius())) return true;
    return false;
  }  
  
  @Override
  double getMass() {
    return mass;
  }
  
  int getID() { return id; }
  void setID(int id) { this.id = id; }
  
  String getName() {
    return name;
  }  
  
  // Mass setter
  void setMass(double mass) {
    this.mass = mass;
  }
  
  @Override
  float getRadius() {
    return radius;
  }
  
  // Radius setter
  void setRadius(float radius) {
    this.radius = radius;
  }
  
  color getColor() {
    return c;
  }  
  
  void setColor(color c) {
    this.c = c;
  }  
  
  @Override
  PVector getPosition() {
    return position.copy();
  }
  
  @Override
  PVector getVelocity() {
    return velocity.copy();
  }
  
  @Override
  PVector addVelocity(PVector add) {
    return velocity.add(add);
  }
  
  /**
    Returns a PVector position that stays bounded to the screen.
  */
  private PVector setPlanetLoc(PVector position, PVector velocity) {
    if(position.x + velocity.x <= 0 && position.y + velocity.y <= 0)
      return new PVector(width, height);
      
     else if(position.x + velocity.x <= 0)
       return new PVector(width, (position.y + velocity.y) % height);
       
     else if(position.y +velocity.y <= 0)
       return new PVector((position.x + velocity.x) % width, height);
       
     else
       return new PVector((position.x + velocity.x) % width, (position.y + velocity.y) % height);
  }
}
