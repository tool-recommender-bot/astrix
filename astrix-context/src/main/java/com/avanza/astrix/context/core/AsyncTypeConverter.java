/*
 * Copyright 2014 Avanza Bank AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package com.avanza.astrix.context.core;

import rx.Observable;
/**
 * 
 * @author Elias Lindholm
 *
 */
public interface AsyncTypeConverter {

	Observable<Object> toObservable(Class<?> fromType, Object asyncTypeInstance);

	Object toAsyncType(Class<?> targetType, Observable<Object> observable);

	boolean canAdaptToType(Class<?> type);

}