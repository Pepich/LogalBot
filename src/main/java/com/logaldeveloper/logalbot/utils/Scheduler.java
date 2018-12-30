/*
 * Copyright (C) 2018 Logan Fick
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.logaldeveloper.logalbot.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {
	private static final ScheduledExecutorService schedulerPool = Executors.newScheduledThreadPool(1);

	public static ScheduledFuture schedule(Runnable runnable, long delay, TimeUnit unit){
		return schedulerPool.schedule(runnable, delay, unit);
	}

	public static ScheduledFuture scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit){
		return schedulerPool.scheduleAtFixedRate(runnable, initialDelay, period, unit);
	}


}