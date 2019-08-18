package deco2800.skyfall.worlds.generation.delaunay;

import deco2800.skyfall.worlds.Tile;
import deco2800.skyfall.worlds.generation.WorldGenException;

import java.lang.Comparable;
import java.util.List;
import java.util.ArrayList;

/**
 * A class used in the world generation procedure to help the world and biomes
 * have a natural looking shape. To see how they are being used, this class is
 * inspired by
 * <a href="http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/?fbclid=IwAR30I7ILTznH6YzYYqZfjIE3vcqPsed85ta9bohPZWi74SfWMwWpD8AVddQ#source">
 *     This</a>
 */
public class WorldGenNode implements Comparable<WorldGenNode> {

    // position
    private double x;
    private double y;

    // List of nodes whose polygon share a line with this nodes polygon
    private List<WorldGenNode> neighbours;

    // List of vertices for this polygon in the form [x, y]
    private List<double[]> vertices;

    // List of tiles that are within the polygon defined by this node
    private List<Tile> tiles;

    // Whether or not this node's polygon is on the edge of the map
    private boolean borderNode;

    /**
     * Constructor for a WorldGenNode
     *
     * @param x the x coordinate of the node
     * @param y the y coordinate of the node
     */
    public WorldGenNode(double x, double y) {
        this.x = x;
        this.y = y;
        this.neighbours = new ArrayList<>();
        this.vertices = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.borderNode = false;
    }

    @Override
    public int compareTo(WorldGenNode other) {
        if (other == null) {
            throw new NullPointerException();
        }
        if (this.getY() == other.getY()) {
            if (this.getX() == other.getX()) {
                return 0;
            }
            if (this.getX() < other.getX()) {
                return -1;
            }
            return 1;
        }
        if (this.getY() < other.getY()) {
            return -1;
        }
        return 1;
    }

    /**
     * Calculates the approximate centroid of the polygon defined by this node.
     * The centroid is approximated as the average position of the vertices of
     * the polygon, rather than integrating to calculate the exact centroid
     *
     * @return The approximate centroid of the polygon defined by this node
     * @throws InvalidCoordinatesException if any vertex's coordinates are not
     *         2 dimensional
     */
    public double[] getCentroid() throws InvalidCoordinatesException {
        double[] centroid = {0, 0};
        // If there are no vertices, return the same position this node is
        // already in
        if (this.vertices.size() == 0) {
            centroid[0] = this.getX();
            centroid[1] = this.getY();
            return centroid;
        }
        for (double[] vertex : this.vertices) {
            if (vertex.length != 2) {
                throw new InvalidCoordinatesException();
            }
            centroid[0] += vertex[0];
            centroid[1] += vertex[1];
        }

        // Currently centroid contains the 'total' coordinates of the vertices.
        // Divide by the number of vertices to get the average coordinates
        centroid[0] /= this.vertices.size();
        centroid[1] /= this.vertices.size();
        return centroid;
    }

    /**
     * Adds another node to this node's list of adjacent nodes
     *
     * @param other the adjacent node
     */
    public void assignNeighbour(WorldGenNode other) {
        this.neighbours.add(other);
    }

    /**
     * Associates a tile with this node
     *
     * @param tile the tile in question
     */
    public void addTile(Tile tile) {
        this.tiles.add(tile);
    }

    /**
     * Uses a variation of Lloyd's Algorithm to make a list of nodes more evenly
     * spread apart.
     *
     * For info on what Lloyd's Algorithm is, see
     * <a href="https://en.wikipedia.org/wiki/Lloyd%27s_algorithm">
     *     the Wikipedia page</a>
     *
     * One key simplification from the method described on Wikipedia used here
     * is approximating the centroid as the average position of the vertices of
     * the polygon, rather than integrating to calculate the exact centroid.
     *
     * @param nodes The list of nodes to apply the Lloyd's Algorithm
     * @param iterations The number of times to apply the algorithm. Too few
     *                   iterations can result in the algorithm not evening out
     *                   the points enough, and too many iterations can
     *                   eliminate the randomness of the node placement
     * @throws WorldGenException if there is an exception thrown when trying
     *         to run Lloyd Relaxation
     */
    public static void lloydRelaxation(List<WorldGenNode> nodes, int iterations,
            int worldSize) throws WorldGenException {
        for (int i = 0; i < iterations; i++) {
            for (WorldGenNode node : nodes) {
                // Don't move border nodes
                if (!node.isBorderNode()) {
                    double[] centroid;
                    centroid = node.getCentroid();
                    node.setCoords(centroid);
                }

                // Remove info that may change when moving nodes
                node.vertices.clear();
                node.neighbours.clear();
                node.tiles.clear();
                node.borderNode = false;
            }
            // Reapply the Delaunay Triangulation
            calculateVertices(nodes, worldSize);
        }
    }

    /**
     * Finds which nodes are neighbours, and assigns them to each other's list
     * of neighbours
     *
     * @param nodes the list of nodes to assign neighbours
     * @throws InvalidCoordinatesException if any nodes have a vertex whose
     *         coordinates are not 2 dimensional
     */
    public static void assignNeighbours(List<WorldGenNode> nodes)
            throws InvalidCoordinatesException {
        // Compare each node with each other node
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                if (isAdjacent(nodes.get(i), nodes.get(j))) {
                    nodes.get(i).assignNeighbour(nodes.get(j));
                    nodes.get(j).assignNeighbour(nodes.get(i));
                }
            }
        }
    }

    /**
     * Determines if two nodes are adjacent. Two nodes are deemed to be adjacent
     * if they share a vertex
     * @param a The first node
     * @param b The second node
     * @return True if the nodes share a vertex, false otherwise
     * @throws InvalidCoordinatesException if one of the WorldGenNodes has
     *         invalid coordinates
     */
    public static boolean isAdjacent(WorldGenNode a, WorldGenNode b)
            throws InvalidCoordinatesException {
        try {
            sharedVertex(a, b);
            // Return true if there wasn't a NotAdjacentException
            return true;
        } catch (NotAdjacentException e) {
            return false;
        }
    }

    /**
     * Finds a shared vertex between two nodes
     *
     * @param a the first node
     * @param b the second node
     * @return the coordinates of the first shared vertex found between the
     *         nodes
     * @throws InvalidCoordinatesException if one of the nodes has a vertex
     *         whose coordinates are not 2 dimensions
     * @throws NotAdjacentException if the nodes don't have a common vertex
     */
    public static double[] sharedVertex(WorldGenNode a, WorldGenNode b)
            throws InvalidCoordinatesException, NotAdjacentException {
        // Compare each vertex of one with each vertex of the other
        for (double[] vertexA : a.getVertices()) {
            if (vertexA.length != 2) {
                throw new InvalidCoordinatesException();
            }
            for (double[] vertexB : b.getVertices()) {
                if (vertexB.length != 2) {
                    throw new InvalidCoordinatesException();
                }
                // If the vertices are sufficiently close (ie the nodes share at
                // least one vertex
                if (vertexA[0] == vertexB[0] && vertexA[1] == vertexB[1]) {
                    return vertexA;
                }
            }
        }
        // Indicate that the points are not adjacent
        throw new NotAdjacentException();
    }

    /**
     * Assigns each tile in the world to the nearest node.
     *
     * @param nodes The list of nodes that can be assigned to
     * @param tiles The list of tiles to assign
     */
    public static void assignTiles(List<WorldGenNode> nodes, List<Tile> tiles) {

        // Ensure nodes are stored in order of Y value
        nodes.sort(Comparable::compareTo);
        for (Tile tile : tiles) {
            // Y coordinate of the tile
            float tileY = tile.getCoordinates().getRow();
            // Find the index of the node with the node with one of the nearest
            // Y values (note, if there is no node with the exact Y value, it)
            // Can choose the node on either side, not the strictly closest one
            int nearestIndex = binarySearch((double) tileY, nodes, 0,
                    nodes.size() - 1);
            boolean lowerLimitFound = false;
            boolean upperLimitFound = false;
            // Store the minimum distance to a node, and the index of that node
            double minDistance = nodes.get(nearestIndex).distanceToTile(tile);
            int minDistanceIndex = nearestIndex;
            int iterations = 1;
            // Starting from the initial index, this loop checks the 1st node on
            // either side, then the 2nd node on either side, continuing
            // outwards (kept track of by iterations).
            while (!(upperLimitFound && lowerLimitFound)) {
                int lower = nearestIndex - iterations;
                int upper = nearestIndex + iterations;
                // Stop the algorithm from checking off the end of the list
                if (lower < 0) {
                    lowerLimitFound = true;
                }
                if (upper > nodes.size() - 1) {
                    upperLimitFound = true;
                }

                if (!lowerLimitFound) {
                    double distance = nodes.get(lower).distanceToTile(tile);
                    // Update the closest node if necessary
                    if (distance < minDistance) {
                        minDistance = distance;
                        minDistanceIndex = lower;
                    }
                    // As distance to a node is necessarily >= the difference in
                    // y value, if the difference in y value is greater than the
                    // smallest distance to a node, all future nodes in that
                    // direction will be further away
                    if (nodes.get(lower).yDistanceToTile(tile) > minDistance) {
                        lowerLimitFound = true;
                    }
                }
                if (!upperLimitFound) {
                    double distance = nodes.get(upper).distanceToTile(tile);
                    if (distance < minDistance) {
                        minDistance = distance;
                        minDistanceIndex = upper;
                    }
                    if (nodes.get(upper).yDistanceToTile(tile) > minDistance) {
                        upperLimitFound = true;
                    }
                }
                iterations++;
            }
            // Assign tile to the node
            nodes.get(minDistanceIndex).addTile(tile);
        }
    }

    /**
     * Returns the square of the distance to the tile
     *
     * @param tile The tile to find the distance to
     * @return The square of the distance
     */
    public double distanceToTile(Tile tile) {
        double[] tileCoords = {tile.getCoordinates().getCol(),
                tile.getCoordinates().getRow()};
        return (Math.pow(this.getX() - tileCoords[0], 2)
                + Math.pow(this.getY() - tileCoords[1], 2));
    }

    /**
     * Returns the square of the difference in y value between this node and the
     * tile
     *
     * @param tile The tile to find the distance to
     * @return The square of the difference in y value
     */
    public double yDistanceToTile(Tile tile) {
        return Math.pow(Math.abs(
                this.getY() - tile.getCoordinates().getRow()), 2);
    }

    private static int binarySearch(double toFind, List<WorldGenNode> nodes,
            int start, int end) {
        double tolerance = 0.0001f;
        int middle = (end + start) / 2;
        double middleValue = nodes.get(middle).getY();
        if (middleValue == toFind || start >= end) {
            return middle;
        }
        if (middleValue < toFind) {
            return binarySearch(toFind, nodes, middle + 1, end);
        }
        return binarySearch(toFind, nodes, start, middle - 1);
    }

    /**
     * Adds the position of this node with another node world gen nodes
     *
     * @param other the other node
     * @return a new node with the added coordinates
     */
    public WorldGenNode add(WorldGenNode other) {
        return new WorldGenNode(this.x + other.getX(),
                this.y + other.getY());
    }

    /**
     * Subtracts the position of another node from this one
     *
     * @param other the other node
     * @return a new node with the subtracted coordinates
     */
    public WorldGenNode subtract(WorldGenNode other) {
        return new WorldGenNode(this.x - other.getX(),
                this.y - other.getY());
    }

    /**
     * Multiplies the node's coordinates by a scalar
     *
     * @param scaleFactor the scalar multiple
     * @return a new node with the multiplied coordinates
     */
    public WorldGenNode scalarMultiply(double scaleFactor) {
        return new WorldGenNode(this.x * scaleFactor, this.y * scaleFactor);
    }

    /**
     * Gets the dot product of the coordinates of this node and another
     *
     * @param other the other node
     * @return the dot product of the coordinates
     */
    public double dotProduct(WorldGenNode other) {
        return this.x * other.getX() + this.y * other.getY();
    }

    /**
     * Gets the k component of the cross product of this node and another
     *
     * @param other the other node
     * @return the k component of the cross product
     */
    public double crossProduct(WorldGenNode other) {
        return this.x * other.getY() - this.y * other.getX();
    }

    /**
     * Gets the magnitude of this node's coordinates
     *
     * @return the distance of this node from the origin
     */
    public double magnitude() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    /**
     * Apply Delaunay Triangulation to a set of nodes
     * Code adapted from <a href="https://github.com/jdiemke/delaunay-triangulator/blob/master/library/src/main/java/io/github/jdiemke/triangulation/DelaunayTriangulator.java">
     *     Johannes Dieme's Implementation</a>
     *
     * @author Johannes Diemke
     * @throws NotEnoughPointsException Thrown when the point set contains less
     *         than three points
     */
     static TriangleSoup triangulate(List<WorldGenNode> nodes)
            throws NotEnoughPointsException {
        TriangleSoup triangleSoup = new TriangleSoup();

        if (nodes == null || nodes.size() < 3) {
            throw new NotEnoughPointsException(
                    "Less than three points in point set.");
        }

        // In order for the in circumcircle test to not consider the vertices of
        // the super triangle we have to start out with a big triangle
        // containing the whole point set. We have to scale the super triangle
        // to be very large. Otherwise the triangulation is not convex.
        double maxOfAnyCoordinate = 0.0d;

        for (WorldGenNode vector : nodes) {
            maxOfAnyCoordinate = Math.max(
                    Math.max(vector.getX(), vector.getY()), maxOfAnyCoordinate);
        }

        maxOfAnyCoordinate *= 16.0d;

        WorldGenNode p1 = new WorldGenNode(0.0d, 3.0d * maxOfAnyCoordinate);
        WorldGenNode p2 = new WorldGenNode(3.0d * maxOfAnyCoordinate, 0.0d);
        WorldGenNode p3 = new WorldGenNode(
                -3.0d * maxOfAnyCoordinate, -3.0d * maxOfAnyCoordinate);

        WorldGenTriangle superTriangle = new WorldGenTriangle(p1, p2, p3);

        triangleSoup.add(superTriangle);

        for (int i = 0; i < nodes.size(); i++) {
            WorldGenTriangle triangle =
                    triangleSoup.findContainingTriangle(nodes.get(i));

            if (triangle == null) {
                // If no containing triangle exists, then the vertex is not
                // inside a triangle (this can also happen due to numerical
                // errors) and lies on an edge. In order to find this edge we
                // search all edges of the triangle soup and select the one
                // which is nearest to the point we try to add. This edge is
                // removed and four new edges are added.
                WorldGenEdge edge = triangleSoup.findNearestEdge(nodes.get(i));

                WorldGenTriangle first =
                        triangleSoup.findOneTriangleSharing(edge);
                WorldGenTriangle second =
                        triangleSoup.findNeighbour(first, edge);

                WorldGenNode firstNoneEdgeVertex =
                        first.getNoneEdgeVertex(edge);
                WorldGenNode secondNoneEdgeVertex =
                        second.getNoneEdgeVertex(edge);

                triangleSoup.remove(first);
                triangleSoup.remove(second);

                WorldGenTriangle triangle1 = new WorldGenTriangle(
                        edge.getA(), firstNoneEdgeVertex, nodes.get(i));
                WorldGenTriangle triangle2 = new WorldGenTriangle(
                        edge.getB(), firstNoneEdgeVertex, nodes.get(i));
                WorldGenTriangle triangle3 = new WorldGenTriangle(
                        edge.getA(), secondNoneEdgeVertex, nodes.get(i));
                WorldGenTriangle triangle4 = new WorldGenTriangle(
                        edge.getB(), secondNoneEdgeVertex, nodes.get(i));

                triangleSoup.add(triangle1);
                triangleSoup.add(triangle2);
                triangleSoup.add(triangle3);
                triangleSoup.add(triangle4);

                legalizeEdge(triangle1, new WorldGenEdge(edge.getA(),
                        firstNoneEdgeVertex), nodes.get(i), triangleSoup);
                legalizeEdge(triangle2, new WorldGenEdge(edge.getB(),
                        firstNoneEdgeVertex), nodes.get(i), triangleSoup);
                legalizeEdge(triangle3, new WorldGenEdge(edge.getA(),
                        secondNoneEdgeVertex), nodes.get(i), triangleSoup);
                legalizeEdge(triangle4, new WorldGenEdge(edge.getB(),
                        secondNoneEdgeVertex), nodes.get(i), triangleSoup);
            } else {
                // The vertex is inside a triangle.
                WorldGenNode a = triangle.getA();
                WorldGenNode b = triangle.getB();
                WorldGenNode c = triangle.getC();

                triangleSoup.remove(triangle);

                WorldGenTriangle first =
                        new WorldGenTriangle(a, b, nodes.get(i));
                WorldGenTriangle second =
                        new WorldGenTriangle(b, c, nodes.get(i));
                WorldGenTriangle third =
                        new WorldGenTriangle(c, a, nodes.get(i));

                triangleSoup.add(first);
                triangleSoup.add(second);
                triangleSoup.add(third);

                legalizeEdge(first, new WorldGenEdge(a, b), nodes.get(i),
                        triangleSoup);
                legalizeEdge(second, new WorldGenEdge(b, c), nodes.get(i),
                        triangleSoup);
                legalizeEdge(third, new WorldGenEdge(c, a), nodes.get(i),
                        triangleSoup);
            }
        }

        // Remove all triangles that contain vertices of the super triangle.
        triangleSoup.removeTrianglesUsing(superTriangle.getA());
        triangleSoup.removeTrianglesUsing(superTriangle.getB());
        triangleSoup.removeTrianglesUsing(superTriangle.getC());

        // Set the borderNode variable for each node in the soup
        triangleSoup.findBorderNodes();

        return triangleSoup;
    }

    /**
     * This method legalizes edges by recursively flipping all illegal edges.
     * Code adapted from <a href="https://github.com/jdiemke/delaunay-triangulator/blob/master/library/src/main/java/io/github/jdiemke/triangulation/DelaunayTriangulator.java">
     *      Johannes Dieme's Implementation</a>
     *
     * @author Johannes Diemke
     * @param triangle The triangle
     * @param edge The edge to be legalized
     * @param newVertex The new vertex
     */
    private static void legalizeEdge(WorldGenTriangle triangle,
            WorldGenEdge edge, WorldGenNode newVertex,
            TriangleSoup triangleSoup) {

        WorldGenTriangle neighbourTriangle
                = triangleSoup.findNeighbour(triangle, edge);

        // If the triangle has a neighbor, then legalize the edge
        if (neighbourTriangle != null) {
            if (neighbourTriangle.isPointInCircumcircle(newVertex)) {
                triangleSoup.remove(triangle);
                triangleSoup.remove(neighbourTriangle);

                WorldGenNode noneEdgeVertex =
                        neighbourTriangle.getNoneEdgeVertex(edge);

                WorldGenTriangle firstTriangle =
                        new WorldGenTriangle(noneEdgeVertex, edge.getA(),
                                newVertex);
                WorldGenTriangle secondTriangle =
                        new WorldGenTriangle(noneEdgeVertex, edge.getB(),
                                newVertex);

                triangleSoup.add(firstTriangle);
                triangleSoup.add(secondTriangle);

                legalizeEdge(firstTriangle, new WorldGenEdge(
                        noneEdgeVertex, edge.getA()), newVertex, triangleSoup);
                legalizeEdge(secondTriangle, new WorldGenEdge(
                        noneEdgeVertex, edge.getB()), newVertex, triangleSoup);
            }
        }
    }

    /**
     * Adds a vertex to the list of vertices for a node
     *
     * @param vertex the vertex to add
     * @throws InvalidCoordinatesException if the vertex's coordinates are not 2
     *         dimensional
     */
    public void addVertex(double[] vertex) throws InvalidCoordinatesException {
        if (vertex.length != 2) {
            throw new InvalidCoordinatesException();
        }
        this.vertices.add(vertex);
    }

    /**
     * Calculates the vertices of each node by converting the Delaunay
     * Triangulation to its equivalent Voronoi Graph
     *
     * @param nodes The nodes to perform the algorithm with
     * @throws WorldGenException if there is an exception thrown when trying
     *         to triangulate the nodes
     */
    public static void calculateVertices(List<WorldGenNode> nodes,
            int worldSize) throws WorldGenException {
        TriangleSoup triangleSoup = triangulate(nodes);
        for (WorldGenTriangle triangle : triangleSoup.getTriangles()) {
            double[] circumcentre = triangle.circumcentre();
            triangle.getA().addVertex(circumcentre);
            triangle.getB().addVertex(circumcentre);
            triangle.getC().addVertex(circumcentre);

            // Make sure all three are border nodes if circumcentre is outside
            // world
            if (Math.abs(circumcentre[0]) > worldSize && Math.abs(
                    circumcentre[1]) > worldSize) {
                triangle.getA().setBorderNode(true);
                triangle.getB().setBorderNode(true);
                triangle.getC().setBorderNode(true);
            }
        }
    }

    /* ------------------------------------------------------------------------
     * 				GETTERS AND SETTERS BELOW THIS COMMENT.
     * ------------------------------------------------------------------------ */

    /**
     * Returns the x value of this node
     *
     * @return the x value of this node
     */
    public double getX() {
        return this.x;
    }

    /**
     * Returns the y value of this node
     *
     * @return the y value of this node
     */
    public double getY() {
        return this.y;
    }

    /**
     * Sets the coordinates of this node
     *
     * @param coords the coordinates to set
     */
    public void setCoords(double[] coords) {
        this.x = coords[0];
        this.y = coords[1];
    }

    /**
     * Get the neighbours of this node
     *
     * @return the neighbours of this node
     */
    public List<WorldGenNode> getNeighbours() {
        return this.neighbours;
    }

    /**
     * Get the vertices of this node
     *
     * @return the vertices of this node
     */
    public List<double[]> getVertices() {
        return this.vertices;
    }

    /**
     * Get the tiles within this node
     *
     * @return the tiles within this node
     */
    public List<Tile> getTiles() {
        return this.tiles;
    }

    /**
     * Returns whether or not this is a border node
     *
     * @return whether or not this is a border node
     */
    public boolean isBorderNode() {
        return this.borderNode;
    }

    /**
     * Sets the border node status of this node
     *
     * @param borderNode whether or not this node is a border node
     */
    public void setBorderNode(boolean borderNode) {
        this.borderNode = borderNode;
    }

}