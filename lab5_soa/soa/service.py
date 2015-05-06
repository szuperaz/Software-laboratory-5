#!/usr/bin/env python

"""
    Software Laboratory 5
    SOA service demo

    This sample service was created for the purposes of demonstrating some
    of the functionality you can achieve by combining the power of three
    Python libraries: cx_Oracle, Flask, and Requests.

    It does not intend to be perfect Python code -- in some places, perfection
    was traded for simplicity, some of these are marked in comments.

    This comment is a so-called docstring, all Python modules and
    functions/methods should have one. Three " or ' characters make it
    possible for multiline strings, and interactive Python environments
    display these "docstrings" (basically header comments) for users of
    your code. Further info: http://www.python.org/dev/peps/pep-0257/
"""

from flask import Flask, jsonify, abort, request
import json
import cx_Oracle
import requests
import re

app = Flask(__name__)

# F1-1
@app.route('/persons.json')
def list_people():
    """Lists all the persons in the database"""
    conn = get_db()
    try:
        cur = conn.cursor()
        try:
            cur.execute('''SELECT person_id, name, address
                        FROM persons
                        ORDER BY name''')

            results = []
            for person_id, name, address in cur:
                results.append({'person_id': person_id, 'name': name, 'address' : address})
            return jsonify(persons=results)
        finally:
            cur.close()
    finally:
        conn.close()

# F1-2
@app.route('/persons/<person_id>.json')
def show_person(person_id):
    """Shows the details of a single person by person_id"""
    conn = get_db()
    try:
        cur = conn.cursor()
        try:
            # F2
            cur.execute("""SELECT person_id, name, address, regexp_substr(address, '^\w*') as city
                        FROM persons
                        WHERE person_id = :id""",
                        id=person_id)
            person = cur.fetchone()
            if person is None:
                # no rows -> 404 Not Found (no need to return manually)
                abort(404)
            else:
                person_result = {'person_id' : person[0], 'name' : person[1], 'address' : person[2]}
                # F2
                city = person[3]
                if city is None:
                    coordinates_result = {'lat' : '-', 'lon' : '-'}
                else:
                    params = {'format' : 'json', 'city' : city, 'country' : 'Hungary'}
                    response = requests.get('http://nominatim.openstreetmap.org/search',
                        params=params)
                    results = response.json()
                    print results
                    if len(results) == 0:
                        coordinates_result = {'lat' : '-', 'lon' : '-'}
                    else:
                        coordinates_result = {'lat' : results[0]['lat'], 'lon' : results[0]['lon']}

            return jsonify(person=person_result, coordinates=coordinates_result)
        finally:
            cur.close()
    finally:
        conn.close()

# F4
@app.route('/persons/by-address/<address>.json')
def search_by_address(address):
    """Searches for persons by address"""
    conn = get_db()
    try:
        cur = conn.cursor()
        try:
            # escaping special characters
            special_characters = ["%", "_"]
            for spec_char in special_characters:
                address = re.sub('\\'+spec_char, '\\\\'+spec_char, address)
            # searching by fragment of address
            address = '%'+address+'%'
            cur.execute("""SELECT person_id, name, address
                        FROM persons
                        WHERE upper(address) LIKE upper(:adr) ESCAPE '\\'""",
                        adr=address)
            results = []
            for person_id, name, address in cur:
                results.append({'person_id': person_id, 'name': name, 'address' : address})
            return jsonify(persons=results)
        finally:
            cur.close()
    finally:
        conn.close()

# F4
@app.route('/persons/by-name/<name>.json')
def search_by_name(name):
    """Searches for persons by name"""
    conn = get_db()
    try:
        cur = conn.cursor()
        try:
            # escaping special characters
            special_characters = ["%", "_"]
            for spec_char in special_characters:
                name = re.sub('\\'+spec_char, '\\\\'+spec_char, name)
            # searching by fragment of address
            name = '%'+name+'%'
            cur.execute("""SELECT person_id, name, address
                        FROM persons
                        WHERE upper(name) LIKE upper(:name) ESCAPE '\\'""",
                        name=name)
            results = []
            for person_id, name, address in cur:
                results.append({'person_id': person_id, 'name': name, 'address' : address})
            return jsonify(persons=results)
        finally:
            cur.close()
    finally:
        conn.close()

# F5
@app.route('/persons/<person_id>', methods=['DELETE'])
def person_delete(person_id):
    """Deletes the person with the given person_id"""
    conn = get_db()
    try:
        cur = conn.cursor()
        try:
            cur.execute("""DELETE persons
                        WHERE person_id = :id""",
                        id=person_id)
            conn.commit()
            # id does not exsist
            if cur.rowcount == 0:
                abort(404)
            data = {'deleted_id' : person_id}
            return jsonify(data=data)
        except cx_Oracle.DatabaseError as exception:
            # invalid number
            if str(exception.message).find('01722') != -1:
                abort(400)
            # if it is probably not a user error
            abort(500)
        finally:
            cur.close()
    finally:
        conn.close()

# F5
@app.route('/persons.json', methods=['POST'])
def person_insert():
    """Inserts a new person"""
    conn = get_db()
    try:
        body = request.json
        if body is None:
            abort(400)
        keys = ["person_id", "name", "address", "phone", "income", "hobby", "favourite_movie"]
        for key in keys:
            if body.get(key) is None:
                body[key] = ""
        cur = conn.cursor()
        try:
            cur.execute("""INSERT INTO persons
                        VALUES(
                            :person_id,
                            :name,
                            :address,
                            :phone,
                            :income,
                            :hobby,
                            :favourite_movie)""",
                        person_id=body["person_id"],
                        name=body["name"],
                        address=body["address"],
                        phone=body["phone"],
                        income=body["income"],
                        hobby=body["hobby"],
                        favourite_movie=body["favourite_movie"])
            conn.commit()
            data = {'inserted_id' : body["person_id"]}
            return jsonify(data=data), 201
        except cx_Oracle.DatabaseError as exception:
            # invalid number
            if str(exception.message).find('01722') != -1:
                abort(400)
            # unique constraint violated
            if str(exception.message).find('00001') != -1:
                abort(400)
            # not nullabel value is null
            if str(exception.message).find('01400') != -1:
                abort(400)
            # if it is probably not a user error
            abort(500)
        finally:
            cur.close()
    finally:
        conn.close()

# F5
@app.route('/persons/<person_id>', methods=['PUT'])
def person_update(person_id):
    """Updates an existing person"""
    conn = get_db()
    try:
        body = request.json
        if body is None:
            abort(400)
        keys = ["person_id", "name", "address", "phone", "income", "hobby", "favourite_movie"]
        update_string = "UPDATE persons SET "
        params = {}
        for key in keys:
            if body.get(key) is not None:
                update_string += key + " = :" + key + ","
                params[key] = body[key]
        # delete last comma
        update_string = update_string[:-1]
        update_string += " WHERE person_id = :person_id_old"
        params["person_id_old"] = person_id
        cur = conn.cursor()
        try:
            cur.execute(update_string, params)
            conn.commit()
            # id does not exsist
            if cur.rowcount == 0:
                abort(404)
            data = {'modified_persons_id' : person_id}
            return jsonify(data=data), 200
        except cx_Oracle.DatabaseError as exception:
            # invalid number
            if str(exception.message).find('01722') != -1:
                abort(400)
            # unique constraint violated
            if str(exception.message).find('00001') != -1:
                abort(400)
            # not nullabel value is null
            if str(exception.message).find('01400') != -1:
                abort(400)
            # FK integrity constraint violated
            if str(exception.message).find('02292') != -1:
                abort(400)
            # if it is probably not a user error
            abort(500)
        finally:
            cur.close()
    finally:
        conn.close()

@app.route('/verbtest.json', methods=['PUT', 'POST'])
def verb_test():
    """Lets you test HTTP verbs different from GET, expects and returns data in JSON format"""
    # it also shows you how to access the method used and the decoded JSON data
    return jsonify(method=request.method, data=request.json, url=request.url)


def get_db():
    """Connects to the RDBMS and returns a connection object"""
    # when used with a `file` object, `with` ensures it gets closed
    with file('config.json') as config_file:
        config = json.load(config_file)
    return cx_Oracle.connect(config['user'], config['pass'], config['host'])


if __name__ == "__main__":
    import os
    os.environ['NLS_LANG'] = '.UTF8'
    app.run(debug=True, port=os.getuid() + 10000)
