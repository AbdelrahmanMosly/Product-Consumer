import { Layer } from 'konva/lib/Layer';
import { Circle } from 'konva/lib/shapes/Circle';
import { Component, OnInit } from '@angular/core';
import Konva from 'konva';
import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ApiService } from '../api/api.service';

@Component({
  selector: 'app-draw',
  templateUrl: './draw.component.html',
  styleUrls: ['./draw.component.css']
})
export class DrawComponent implements OnInit {

  constructor(private api: ApiService) {
    this.stage.add(this.layer);
  }

  static numQ: number = 1;
  static numM: number = 1;
  static x1: number;
  static y1: number;
  static x2: number;
  static y2: number;
  static intialSet = false;
  stage = new Konva.Stage({
    container: 'container',
    width: window.innerWidth ,
    height: window.innerHeight * 0.85,
  });
  layer = new Konva.Layer();


  queue(){
    this.api.send("/makeQueue").subscribe();
    var rectangle = new Konva.Group({
      x: 25,
      y: 25,
      width: 130,
      height: 30,
      draggable: true,
  });

  rectangle.add(new Konva.Rect({
      width: 130,
      height: 30,
      fill: 'lightblue'
  }));

  rectangle.add(new Konva.Text({
      text: 'Q' + DrawComponent.numQ,
      fontSize: 25,
      fontFamily: 'Calibri',
      fill: '#000',
      width: 130,
      padding: 5,
      align: 'center'
  }));
  DrawComponent.numQ++;
  rectangle.on('click', function(evt){
    if(!DrawComponent.intialSet){
      DrawComponent.x1 = evt.target.absolutePosition().x + evt.target.width()/3;
      DrawComponent.y1 = evt.target.absolutePosition().y + evt.target.height()/3;
      DrawComponent.intialSet = !DrawComponent.intialSet;
    }
    else{
      DrawComponent.x2 = evt.target.absolutePosition().x + evt.target.width()/3;
      DrawComponent.y2 = evt.target.absolutePosition().y + evt.target.height()/3;
      DrawComponent.intialSet = !DrawComponent.intialSet;
    }
  })
  rectangle.on('dblclick', function(evt){
    this.destroy();
    DrawComponent.numQ--;
  })
  this.layer.add(rectangle);
  }

  machine(){
    this.api.send("/makeMachine", 1).subscribe();
    var circle = new Konva.Group({
      x: 100,
      y: 100,
      width: 50,
      height:50,
      draggable: true,
    })


    circle.add(new Konva.Circle({
      x:25,
      y:20,
      radius: 50,
      fill: 'cyan',
    }));

    circle.add(new Konva.Text({
        text: 'M' + DrawComponent.numM,
        fontSize: 25,
        fontFamily: 'Calibri',
        fill: '#000',
        padding: 5,
    }));
    DrawComponent.numM++;

    circle.on('click', function(evt){
      if(!DrawComponent.intialSet){
        DrawComponent.x1 = evt.target.absolutePosition().x;
        DrawComponent.y1 = evt.target.absolutePosition().y;
        DrawComponent.intialSet = !DrawComponent.intialSet;
      }
      else{
        DrawComponent.x2 = evt.target.absolutePosition().x;
        DrawComponent.y2 = evt.target.absolutePosition().y;
        DrawComponent.intialSet = !DrawComponent.intialSet;
      }
    })
    circle.on('dblclick', function(evt){
      this.destroy()
      DrawComponent.numM--;
    })
    this.layer.add(circle);
  }

  connection(){
    this.api.get("/push").subscribe();
    const dx = DrawComponent.x2 - DrawComponent.x1;
    const dy = DrawComponent.y2 - DrawComponent.y1;
    let angle = Math.atan2(-dy, dx);
    const radius = 60;
    var arrow = new Konva.Arrow({
      x: 0,
      y: 0,
      points: [DrawComponent.x1 + -radius * Math.cos(angle + Math.PI), DrawComponent.y1 + radius * Math.sin(angle + Math.PI), DrawComponent.x2+ -radius * Math.cos(angle), DrawComponent.y2+ radius * Math.sin(angle)],
      pointerLength: 10,
      pointerWidth: 10,
      fill: 'black',
      stroke: 'black',
      strokeWidth: 2,
      draggable:true
    })
    arrow.on('dblclick', function(evt){
      this.destroy()
    })
    this.layer.add(arrow);

  }
  ngOnInit(): void {
    let url = "http://localhost:8080/subscribe";
    let eventSource = new EventSource(url);
  }
}
