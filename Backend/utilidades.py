import requests
import base64
from geopy.geocoders import Nominatim
from geopy.exc import GeocoderTimedOut, GeocoderUnavailable
import time


def imagenABase64(urlImagen):
    try:
        response = requests.get(urlImagen)
        if response.status_code == 200:
            image_data = response.content
            base64_data = base64.b64encode(image_data).decode('utf-8')
            return base64_data
        else:
            return None
    except Exception as e:
        return None

def obtenerCoordenadas(direccion):
    geolocator = Nominatim(user_agent="mi_app_de_geolocalizacion")

    try:
        location = geolocator.geocode(direccion, country_codes="AR")
        
    except (GeocoderTimedOut, GeocoderUnavailable) as e:
        location = None
        
    if location:
        latitud = location.latitude
        longitud = location.longitude
    else:
        latitud = "No posee coordenadas"
        longitud = "No posee coordenadas"
    
    return latitud, longitud
