package com.dynamicdrawingtoolkit.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import com.dynamicdrawingtoolkit.drawingobject.*;
import com.dynamicdrawingtoolkit.drawingobject.Rectangle;

public class DrawingCanvas extends JPanel {

    public static final int SHAPE_LINE = 1;
    public static final int SHAPE_CIRCLE = 2;
    public static final int SHAPE_OVAL = 3;
    public static final int SHAPE_RECTANGLE = 4;

    public static final int STATE_DRAW = 1;
    public static final int STATE_EDIT = 2;

    private ArrayList<DrawingObject> shapes;
    private int state;
    private int currentShapeType;
    private DrawingObject currentShapeObject;
    private Color currentShapeColor;

    public JLabel statusLabel;
    
    public DrawingCanvas(JLabel statusLabel) {
        this.shapes = new ArrayList<DrawingObject>();
        this.currentShapeType = SHAPE_LINE;
        this.currentShapeObject = null;
        this.currentShapeColor = Color.BLACK;

        this.statusLabel = statusLabel;
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        this.add(this.statusLabel, BorderLayout.SOUTH);
        
        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (DrawingObject shape : shapes) shape.draw(g);
        if (this.currentShapeObject != null) {
            switch (this.state) {
                case STATE_DRAW:
                    this.currentShapeObject.draw(g);
                    break;
                case STATE_EDIT:
                    this.currentShapeObject.drawBoundingBox(g);
                    break;
            }
        }
    }

    public void clearDrawing() {
        this.shapes.clear();
        this.currentShapeObject = null;
        repaint();
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setCurrentShapeType(int type) {
        this.currentShapeType = type;
    }

    private class MouseHandler extends MouseAdapter {
        
        @Override
        public void mousePressed(MouseEvent e) {
            switch (state) {
                case STATE_DRAW:
                switch (currentShapeType) {
                    case SHAPE_LINE:
                    currentShapeObject = new Line(e.getX(), e.getY(),
                        e.getX(), e.getY(), currentShapeColor);
                    break;

                    case SHAPE_CIRCLE:
                    currentShapeObject = new Circle(e.getX(), e.getY(),
                        e.getX(), e.getY(), currentShapeColor);
                    break;

                    case SHAPE_OVAL:
                    currentShapeObject = new Oval(e.getX(), e.getY(),
                        e.getX(), e.getY(), currentShapeColor);
                    break;

                    case SHAPE_RECTANGLE:
                    currentShapeObject = new Rectangle(e.getX(), e.getY(),
                        e.getX(), e.getY(), currentShapeColor);
                    break;
                }
                break;

                case STATE_EDIT:
                boolean found = false;
                for (int i = shapes.size()-1; i >= 0; i--) {
                    if (shapes.get(i).isCollide(e.getX(), e.getY())) {
                        currentShapeObject = shapes.get(i);
                        currentShapeObject.setOffsetX(
                            e.getX() -
                            currentShapeObject.getUpperLeftX()
                            );
                        currentShapeObject.setOffsetY(
                            e.getY() -
                            currentShapeObject.getUpperLeftY()
                            );
                        found = true;
                        break;
                    }
                }
                if (!found) currentShapeObject = null;
                break;
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            switch (state) {
                case STATE_DRAW:
                currentShapeObject.setX2(e.getX());
                currentShapeObject.setY2(e.getY());
                shapes.add(currentShapeObject);
                currentShapeObject = null;
                break;

                case STATE_EDIT:
                break;
            }

            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            statusLabel.setText(String.format(
                "Mouse Coordinates X: %d Y: %d",
                e.getX(), e.getY())
                );
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            switch (state) {
                case STATE_DRAW:
                currentShapeObject.setX2(e.getX());
                currentShapeObject.setY2(e.getY());
                break;

                case STATE_EDIT:
                if (currentShapeObject == null) break;
                int width = currentShapeObject.getWidth();
                int height = currentShapeObject.getHeight();
                currentShapeObject.setX1(
                    e.getX() - 
                    currentShapeObject.getOffsetX()
                    );
                currentShapeObject.setY1(
                    e.getY() - 
                    currentShapeObject.getOffsetY()
                    );
                currentShapeObject.setX2(
                    e.getX() + width - 
                    currentShapeObject.getOffsetX()
                    );
                currentShapeObject.setY2(
                    e.getY() + height - 
                    currentShapeObject.getOffsetY()
                    );
                break;
            }

            statusLabel.setText(String.format(
                "Mouse Coordinates X: %d Y: %d",
                e.getX(), e.getY())
                );
            repaint();
        }
    }
}
