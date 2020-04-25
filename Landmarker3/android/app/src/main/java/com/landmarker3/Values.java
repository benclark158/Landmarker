package com.landmarker3;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Values {

    public static List<String> makeLabels(){
        List<String> labels = new LinkedList<>();

        labels.add("Burren"); //http://commons.wikimedia.org/wiki/Category:Burren
        labels.add("Brighton_Pier"); //http://commons.wikimedia.org/wiki/Category:Brighton_Pier
        labels.add("Brompton_Cemetery"); //http://commons.wikimedia.org/wiki/Category:Brompton_Cemetery
        labels.add("River_Severn"); //http://commons.wikimedia.org/wiki/Category:River_Severn
        labels.add("RHS_Wisley"); //http://commons.wikimedia.org/wiki/Category:RHS_Wisley
        labels.add("St_Deiniol's_Church_Hawarden"); //http://commons.wikimedia.org/wiki/Category:St_Deiniol's_Church,_Hawarden
        labels.add("Forest_of_Dean"); //http://commons.wikimedia.org/wiki/Category:Forest_of_Dean
        labels.add("Glasnevin_Cemetery"); //http://commons.wikimedia.org/wiki/Category:Glasnevin_Cemetery
        labels.add("Hadrian's_Wall"); //http://commons.wikimedia.org/wiki/Category:Hadrian's_Wall
        labels.add("Lancaster_Canal"); //http://commons.wikimedia.org/wiki/Category:Lancaster_Canal
        labels.add("Lindisfarne"); //http://commons.wikimedia.org/wiki/Category:Lindisfarne
        labels.add("Tower_Bridge"); //http://commons.wikimedia.org/wiki/Category:Tower_Bridge
        labels.add("Skomer_Island"); //http://commons.wikimedia.org/wiki/Category:Skomer_Island
        labels.add("Olympic_Stadium_(London)"); //http://commons.wikimedia.org/wiki/Category:Olympic_Stadium_(London)
        labels.add("Tyne_Bridge"); //http://commons.wikimedia.org/wiki/Category:Tyne_Bridge
        labels.add("Jurassic_Coast"); //http://commons.wikimedia.org/wiki/Category:Jurassic_Coast
        labels.add("Brockhampton_Estate"); //http://commons.wikimedia.org/wiki/Category:Brockhampton_Estate
        labels.add("Heskin_Hall"); //http://commons.wikimedia.org/wiki/Category:Heskin_Hall
        labels.add("Blickling_Hall"); //http://commons.wikimedia.org/wiki/Category:Blickling_Hall
        labels.add("Snizort"); //http://commons.wikimedia.org/wiki/Category:Snizort
        labels.add("Arthur's_Seat"); //http://commons.wikimedia.org/wiki/Category:Arthur's_Seat
        labels.add("Pilkington_Library"); //http://commons.wikimedia.org/wiki/Category:Pilkington_Library
        labels.add("Cambridge_University_Botanic_Garden"); //http://commons.wikimedia.org/wiki/Category:Cambridge_University_Botanic_Garden
        labels.add("Exe_Estuary"); //http://commons.wikimedia.org/wiki/Category:Exe_Estuary
        labels.add("Victoria_Memorial_London"); //http://commons.wikimedia.org/wiki/Category:Victoria_Memorial,_London
        labels.add("Erddig"); //http://commons.wikimedia.org/wiki/Category:Erddig
        labels.add("Barrow_Hill_Engine_Shed"); //http://commons.wikimedia.org/wiki/Category:Barrow_Hill_Engine_Shed
        labels.add("River_Exe"); //http://commons.wikimedia.org/wiki/Category:River_Exe
        labels.add("Beverley_Minster"); //http://commons.wikimedia.org/wiki/Category:Beverley_Minster
        labels.add("Legoland_Windsor"); //http://commons.wikimedia.org/wiki/Category:Legoland_Windsor
        labels.add("Southern_Upland_Way"); //http://commons.wikimedia.org/wiki/Category:Southern_Upland_Way
        labels.add("Cliffs_of_Moher"); //http://commons.wikimedia.org/wiki/Category:Cliffs_of_Moher
        labels.add("Staffordshire_and_Worcestershire_Canal"); //http://commons.wikimedia.org/wiki/Category:Staffordshire_and_Worcestershire_Canal
        labels.add("Oxford_Canal"); //http://commons.wikimedia.org/wiki/Category:Oxford_Canal
        labels.add("Conwy_Castle"); //http://commons.wikimedia.org/wiki/Category:Conwy_Castle
        labels.add("Hyde_Park_London"); //http://commons.wikimedia.org/wiki/Category:Hyde_Park,_London
        labels.add("Cardiff_Bay"); //http://commons.wikimedia.org/wiki/Category:Cardiff_Bay
        labels.add("White_Cliffs_of_Dover"); //http://commons.wikimedia.org/wiki/Category:White_Cliffs_of_Dover
        labels.add("Raglan_Castle"); //http://commons.wikimedia.org/wiki/Category:Raglan_Castle
        labels.add("Stonehenge"); //http://commons.wikimedia.org/wiki/Category:Stonehenge
        labels.add("Tintern_Abbey_Wales"); //http://commons.wikimedia.org/wiki/Category:Tintern_Abbey,_Wales
        labels.add("Forth_Bridge"); //http://commons.wikimedia.org/wiki/Category:Forth_Bridge
        labels.add("Bolingbroke_Castle"); //http://commons.wikimedia.org/wiki/Category:Bolingbroke_Castle
        labels.add("D%C3%BAn_Laoghaire"); //http://commons.wikimedia.org/wiki/Category:D%C3%BAn_Laoghaire
        labels.add("Brookwood_Cemetery"); //http://commons.wikimedia.org/wiki/Category:Brookwood_Cemetery
        labels.add("Royal_Albert_Dock_Liverpool"); //http://commons.wikimedia.org/wiki/Category:Royal_Albert_Dock,_Liverpool
        labels.add("Caernarfon_Castle"); //http://commons.wikimedia.org/wiki/Category:Caernarfon_Castle
        labels.add("River_Nene"); //http://commons.wikimedia.org/wiki/Category:River_Nene
        labels.add("Offa's_Dyke_Path"); //http://commons.wikimedia.org/wiki/Category:Offa's_Dyke_Path
        labels.add("West_Norwood_Cemetery"); //http://commons.wikimedia.org/wiki/Category:West_Norwood_Cemetery
        labels.add("Llangollen_Canal"); //http://commons.wikimedia.org/wiki/Category:Llangollen_Canal
        labels.add("Greensand_Way"); //http://commons.wikimedia.org/wiki/Category:Greensand_Way
        labels.add("Giant's_Causeway"); //http://commons.wikimedia.org/wiki/Category:Giant's_Causeway
        labels.add("River_Mersey"); //http://commons.wikimedia.org/wiki/Category:River_Mersey
        labels.add("Anglesey_Abbey"); //http://commons.wikimedia.org/wiki/Category:Anglesey_Abbey
        labels.add("Windermere"); //http://commons.wikimedia.org/wiki/Category:Windermere
        labels.add("Glendalough"); //http://commons.wikimedia.org/wiki/Category:Glendalough
        labels.add("Pennine_Way"); //http://commons.wikimedia.org/wiki/Category:Pennine_Way
        labels.add("River_Clyde"); //http://commons.wikimedia.org/wiki/Category:River_Clyde
        labels.add("Burrator"); //http://commons.wikimedia.org/wiki/Category:Burrator
        labels.add("Imperial_War_Museum_Duxford"); //http://commons.wikimedia.org/wiki/Category:Imperial_War_Museum_Duxford
        labels.add("Trans_Pennine_Trail"); //http://commons.wikimedia.org/wiki/Category:Trans_Pennine_Trail
        labels.add("Isle_of_Portland"); //http://commons.wikimedia.org/wiki/Category:Isle_of_Portland
        labels.add("Hampstead_Cemetery"); //http://commons.wikimedia.org/wiki/Category:Hampstead_Cemetery
        labels.add("Nunhead_cemetery"); //http://commons.wikimedia.org/wiki/Category:Nunhead_cemetery
        labels.add("Port_of_London"); //http://commons.wikimedia.org/wiki/Category:Port_of_London
        labels.add("Royal_Botanic_Garden_Edinburgh"); //http://commons.wikimedia.org/wiki/Category:Royal_Botanic_Garden_Edinburgh
        labels.add("Hills_of_Dumfries_and_Galloway"); //http://commons.wikimedia.org/wiki/Category:Hills_of_Dumfries_and_Galloway
        labels.add("Whitby_Abbey"); //http://commons.wikimedia.org/wiki/Category:Whitby_Abbey
        labels.add("Black_Mountains_Wales"); //http://commons.wikimedia.org/wiki/Category:Black_Mountains,_Wales
        labels.add("Loch_Lomond"); //http://commons.wikimedia.org/wiki/Category:Loch_Lomond
        labels.add("Glen_of_Imaal"); //http://commons.wikimedia.org/wiki/Category:Glen_of_Imaal
        labels.add("Edinburgh_Castle"); //http://commons.wikimedia.org/wiki/Category:Edinburgh_Castle
        labels.add("Plymouth_Hoe"); //http://commons.wikimedia.org/wiki/Category:Plymouth_Hoe
        labels.add("Beamish_Museum"); //http://commons.wikimedia.org/wiki/Category:Beamish_Museum
        labels.add("River_Trent"); //http://commons.wikimedia.org/wiki/Category:River_Trent
        labels.add("St._James's_Park"); //http://commons.wikimedia.org/wiki/Category:St._James's_Park
        labels.add("Didcot_Railway_Centre"); //http://commons.wikimedia.org/wiki/Category:Didcot_Railway_Centre
        labels.add("Mardale"); //http://commons.wikimedia.org/wiki/Category:Mardale
        labels.add("Caerphilly_Castle"); //http://commons.wikimedia.org/wiki/Category:Caerphilly_Castle
        labels.add("Mount_Jerome_Cemetery"); //http://commons.wikimedia.org/wiki/Category:Mount_Jerome_Cemetery
        labels.add("Kennet_and_Avon_Canal"); //http://commons.wikimedia.org/wiki/Category:Kennet_and_Avon_Canal
        labels.add("Trent Building"); //
        labels.add("Portland Building"); //

        //=CONCAT("labels.add('",D67,"'); //", C67)

        return labels;
    }

    public static HashMap<String, LatLng> getCoords(){
        HashMap<String, LatLng> map = new HashMap<>();

        map.put("Burren", new LatLng(53.0078,-9.0022));
        map.put("Brighton_Pier", new LatLng(50.8164,-0.1372));
        map.put("Brompton_Cemetery", new LatLng(51.484882,-0.190796));
        map.put("River_Severn", new LatLng(51.6853,-2.5436));
        map.put("RHS_Wisley", new LatLng(51.313,-0.474));
        map.put("St_Deiniol's_Church_Hawarden", new LatLng(53.1859,-3.02588));
        map.put("Forest_of_Dean", new LatLng(51.79,-2.54));
        map.put("Glasnevin_Cemetery", new LatLng(53.3722,-6.2778));
        map.put("Hadrian's_Wall", new LatLng(55.0242,-2.2925));
        map.put("Lancaster_Canal", new LatLng(54.0667,-2.8));
        map.put("Lindisfarne", new LatLng(55.67806,-1.79556));
        map.put("Tower_Bridge", new LatLng(51.5055,-0.075406));
        map.put("Skomer_Island", new LatLng(51.7361,-5.2963));
        map.put("Olympic_Stadium_(London)", new LatLng(51.5386,-0.0164));
        map.put("Tyne_Bridge", new LatLng(54.96819,-1.606254));
        map.put("Jurassic_Coast", new LatLng(50.705555,-2.989889));
        map.put("Brockhampton_Estate", new LatLng(52.2013,-2.4586));
        map.put("Heskin_Hall", new LatLng(53.6352,-2.7187));
        map.put("Blickling_Hall", new LatLng(52.8117,1.23168));
        map.put("Snizort", new LatLng(57.525,-6.358));
        map.put("Arthur's_Seat", new LatLng(55.9442,-3.1619));
        map.put("Pilkington_Library", new LatLng(52.763,-1.237));
        map.put("Cambridge_University_Botanic_Garden", new LatLng(52.1938,0.1279));
        map.put("Exe_Estuary", new LatLng(50.63,-3.43));
        map.put("Victoria_Memorial_London", new LatLng(51.50183,-0.14064));
        map.put("Erddig", new LatLng(53.0272,-3.0066));
        map.put("Barrow_Hill_Engine_Shed", new LatLng(53.274498,-1.381821));
        map.put("River_Exe", new LatLng(50.6147,-3.425));
        map.put("Beverley_Minster", new LatLng(53.8392,-0.4247));
        map.put("Legoland_Windsor", new LatLng(51.4635,-0.6511));
        map.put("Southern_Upland_Way", new LatLng(55.4667,-3.2));
        map.put("Cliffs_of_Moher", new LatLng(52.9361,-9.4708));
        map.put("Staffordshire_and_Worcestershire_Canal", new LatLng(52.8,-2));
        map.put("Oxford_Canal", new LatLng(52.45,-1.4667));
        map.put("Conwy_Castle", new LatLng(53.28,-3.83));
        map.put("Hyde_Park_London", new LatLng(51.507328,-0.169644));
        map.put("Cardiff_Bay", new LatLng(51.463,-3.164));
        map.put("White_Cliffs_of_Dover", new LatLng(51.15,1.32));
        map.put("Raglan_Castle", new LatLng(51.770298,-2.850063));
        map.put("Stonehenge", new LatLng(51.1788,-1.8262));
        map.put("Tintern_Abbey_Wales", new LatLng(51.6971,-2.67722));
        map.put("Forth_Bridge", new LatLng(55.9984,-3.3876));
        map.put("Bolingbroke_Castle", new LatLng(53.1651,0.0169));
        map.put("D%C3%BAn_Laoghaire", new LatLng(53.3,-6.14));
        map.put("Brookwood_Cemetery", new LatLng(51.2967,-0.6333));
        map.put("Royal_Albert_Dock_Liverpool", new LatLng(53.4003,-2.9927));
        map.put("Caernarfon_Castle", new LatLng(53.13931,-4.27689));
        map.put("River_Nene", new LatLng(52.5648,-0.355));
        map.put("Offa's_Dyke_Path", new LatLng(52.3458,-3.0517));
        map.put("West_Norwood_Cemetery", new LatLng(51.433,-0.0981));
        map.put("Llangollen_Canal", new LatLng(52.9722,-3.1711));
        map.put("Greensand_Way", new LatLng(51.1772,-0.3758));
        map.put("Giant's_Causeway", new LatLng(55.240833,-6.511667));
        map.put("River_Mersey", new LatLng(53.41431,-2.15667));
        map.put("Anglesey_Abbey", new LatLng(52.237,0.24));
        map.put("Windermere", new LatLng(54.3583,-2.9361));
        map.put("Glendalough", new LatLng(53.0103,-6.3275));
        map.put("Pennine_Way", new LatLng(53.3706,-1.8168));
        map.put("River_Clyde", new LatLng(55.714733,-4.982529));
        map.put("Burrator", new LatLng(50.4833,-4.0333));
        map.put("Imperial_War_Museum_Duxford", new LatLng(52.0931,0.1294));
        map.put("Trans_Pennine_Trail", new LatLng(53.5152,-1.3689));
        map.put("Isle_of_Portland", new LatLng(50.55,-2.44));
        map.put("Hampstead_Cemetery", new LatLng(51.5553,-0.2));
        map.put("Nunhead_cemetery", new LatLng(51.46423,-0.05304));
        map.put("Port_of_London", new LatLng(51.5,0.05));
        map.put("Royal_Botanic_Garden_Edinburgh", new LatLng(55.965,-3.21));
        map.put("Hills_of_Dumfries_and_Galloway", new LatLng(55.217,-4.017));
        map.put("Whitby_Abbey", new LatLng(54.488,-0.607508));
        map.put("Black_Mountains_Wales", new LatLng(51.95,-3.1));
        map.put("Loch_Lomond", new LatLng(56.0833,-4.5667));
        map.put("Glen_of_Imaal", new LatLng(53.0089,-6.4653));
        map.put("Edinburgh_Castle", new LatLng(55.9487,-3.20073));
        map.put("Plymouth_Hoe", new LatLng(50.3644,-4.1422));
        map.put("Beamish_Museum", new LatLng(54.8819,-1.6583));
        map.put("River_Trent", new LatLng(53.7,-0.7));
        map.put("St._James's_Park", new LatLng(51.5017,-0.1319));
        map.put("Didcot_Railway_Centre", new LatLng(51.6135,-1.2448));
        map.put("Mardale", new LatLng(54.51,-2.81));
        map.put("Caerphilly_Castle", new LatLng(51.576054,-3.220333));
        map.put("Mount_Jerome_Cemetery", new LatLng(53.324696,-6.284166));
        map.put("Kennet_and_Avon_Canal", new LatLng(51.375,-2.3022));
        map.put("Trent Building", new LatLng(52.93692,-1.196088));
        map.put("Portland Building", new LatLng(52.93827,-1.194443));

        //=CONCAT("map.put('", D67, ", new LatLng(", E67,",",F67,"));")

        return map;
    }
}
