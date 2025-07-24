



# Ajout de la BD dans mon code 

from fastapi import FastAPI, HTTPException, Path
from dataclasses import asdict, dataclass
import json


with open("acteurs.json", "r") as f: #Ouvrir le jasonFile en mode lecture 
    acteurs_list = json.load(f)  #lecture du fichier f et conversion en objet python 


list_acteurs = {k+1:v for k, v in enumerate(acteurs_list)}

@dataclass
class Acteur():                     #classe modele 
    id:int 
    name: str 
    bio: str 
    picture: str

app = FastAPI() #Faire appel a fast api

# Donner le nombre de documents 
@app.get("/total_acteurs")
def get_total_acteurs()-> dict:
    return{"total":len(list_acteurs)}

# Endpoint GET 
@app.get("/acteurs")
def get_all_acteurs()->list[Acteur]:
    res = []
    for id in list_acteurs: 
        res.append(Acteur(**list_acteurs[id])) # convertir un dictionnaire en objet grace a l'operateur **

    return res

# permet de recuperer un acteur en fonction de son id 
@app.get("/acteur/{id}")
def get_acteur_by_id(id:int=Path(ge=1))->Acteur: # ge veut dire >=
    if id not in list_acteurs:  #Verifier que la recherche est dans l'intervalle [1;6]
        raise HTTPException(status_code=404,detail="Cet acteur n'existe pas")
    return Acteur(**list_acteurs[id])

# Creation d'un acteur 
@app.post("/acteur/")
def create_acteur(acteur:Acteur)->Acteur:
    if acteur.id in list_acteurs:
        raise HTTPException(status_code=404, detail=f"Cet acteur {acteur.id} existe deja")
    list_acteurs[acteur.id]=asdict(acteur) #asdict transforme un objet en dictionnaire 
    return acteur 
