package com.example.haipingguo.questionview.view.bean;

public class ModulePosition {
    //答案热区下标
    public int index;
    public Position leftTop;
    public Position rightBottom;
    public Position centerPosition;

    public void setCenterPosition(Position centerPosition) {
        this.centerPosition = centerPosition;
    }

    public ModulePosition(int index, Position leftTop, Position rightBottom) {
        this.index = index;
        this.leftTop = leftTop;
        this.rightBottom = rightBottom;
    }

    public ModulePosition() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Position getLeftTop() {
        return leftTop;
    }

    public void setLeftTop(Position leftTop) {
        this.leftTop = leftTop;
    }

    public Position getRightBottom() {
        return rightBottom;
    }

    public void setRightBottom(Position rightBottom) {
        this.rightBottom = rightBottom;
    }

    public Position getCenterPosition() {
        return centerPosition;
    }
}
