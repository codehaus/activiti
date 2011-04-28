/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.service.impl.json;

import java.lang.reflect.Field;

import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public abstract class FieldMapper {

  protected ClassMapper classMapper;
  protected Field field;
  
  public FieldMapper(ClassMapper classMapper, Field field) {
    this.classMapper = classMapper;
    this.field = field;
  }

  public abstract void toBean(DBObject dbObject, Object bean, Object parentBean) throws Exception;
  public abstract void toJson(DBObject dbObject, Object bean) throws Exception;
}
