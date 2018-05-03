# Binder 源码分析

## AIDL
### SDK自动生成的AIDL类
- **DESCRIPTOR**

	`private static final java.lang.String DESCRIPTOR = "com.rxt.bindersample.aidl.ICellPhoneManager";`

	> Binder的唯一标识

----------
<br>
<br>

- **asInterface**

		/**
         * Cast an IBinder object into an com.rxt.bindersample.aidl.ICellPhoneManager interface,
         * generating a proxy if needed.
         */
        public static com.rxt.bindersample.aidl.ICellPhoneManager asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.rxt.bindersample.aidl.ICellPhoneManager))) {
                return ((com.rxt.bindersample.aidl.ICellPhoneManager) iin);
            }
            return new com.rxt.bindersample.aidl.ICellPhoneManager.Stub.Proxy(obj);
        }

	> 将服务端的Binder转换成客户端的AIDL接口对象，当服务端和客户端在同一进程中时，返回服务端的Stub，反之，返回系统封装的Proxy代理对象

----------
<br>
<br>

- **asBinder**

		public android.os.IBinder asBinder() {
            return this;
        }

	> 返回Binder对象

----------
<br>
<br>

- **onTransact**

		public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
	            switch (code) {
	                case INTERFACE_TRANSACTION: {
	                    reply.writeString(DESCRIPTOR);
	                    return true;
	                }
	                case TRANSACTION_getCellPhoneList: {
	                    data.enforceInterface(DESCRIPTOR);
	                    java.util.List<com.rxt.bindersample.aidl.CellPhone> _result = this.getCellPhoneList();
	                    reply.writeNoException();
	                    reply.writeTypedList(_result);
	                    return true;
	                }
	                case TRANSACTION_addCellPhone: {
	                    data.enforceInterface(DESCRIPTOR);
	                    com.rxt.bindersample.aidl.CellPhone _arg0;
	                    if ((0 != data.readInt())) {
	                        _arg0 = com.rxt.bindersample.aidl.CellPhone.CREATOR.createFromParcel(data);
	                    } else {
	                        _arg0 = null;
	                    }
	                    this.addCellPhone(_arg0);
	                    reply.writeNoException();
	                    return true;
	                }
	                case TRANSACTION_registerOnNewDataReceivedListner: {
	                    data.enforceInterface(DESCRIPTOR);
	                    com.rxt.bindersample.aidl.IOnNewDataReceivedListner _arg0;
	                    _arg0 = com.rxt.bindersample.aidl.IOnNewDataReceivedListner.Stub.asInterface(data.readStrongBinder());
	                    this.registerOnNewDataReceivedListner(_arg0);
	                    reply.writeNoException();
	                    return true;
	                }
	                case TRANSACTION_unRegisterOnNewDataReceivedListner: {
	                    data.enforceInterface(DESCRIPTOR);
	                    com.rxt.bindersample.aidl.IOnNewDataReceivedListner _arg0;
	                    _arg0 = com.rxt.bindersample.aidl.IOnNewDataReceivedListner.Stub.asInterface(data.readStrongBinder());
	                    this.unRegisterOnNewDataReceivedListner(_arg0);
	                    reply.writeNoException();
	                    return true;
	                }
	            }
	            return super.onTransact(code, data, reply, flags);
	        }
	> 该方法运行在服务端，当客户端向服务端发起跨进程请求时调用。code为方法的唯一标识，服务端根据code来确定调用哪个方法；data为客户端传输的数据，服务端取出data中的数据执行目标方法，然后向reply中写入返回数据。该方法返回false时会导致客户端请求失败，由此可在服务端service的Binder对象中重写此方法进行客户端权限验证等操作

		public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
		    //客户端权限验证
		    int permissionVal = checkCallingOrSelfPermission("com.rxt.binder");
		    if (permissionVal == PackageManager.PERMISSION_DENIED) {
		        //客户端没有声明权限
		        Log.i(TAG, "onTransact: " + "客户端没有权限");
		        return false;
		    }
		
		    //客户端包名验证
		    String packageName = null;
		    String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
		    if (packages != null && packages.length > 0) {
		        packageName = packages[0];
		    }
		    if (packageName != null && !packageName.startsWith("com.rxt")) {
		        Log.i(TAG, "onTransact: " + "客户端包名不对");
		        return false;
		    }
		    return super.onTransact(code, data, reply, flags);
		}


----------
<br>
<br>

- **Proxy中实现的接口中的方法**

	如：

		public java.util.List<com.rxt.bindersample.aidl.CellPhone> getCellPhoneList() throws android.os.RemoteException {
            android.os.Parcel _data = android.os.Parcel.obtain();
            android.os.Parcel _reply = android.os.Parcel.obtain();
            java.util.List<com.rxt.bindersample.aidl.CellPhone> _result;
            try {
                _data.writeInterfaceToken(DESCRIPTOR);
                mRemote.transact(Stub.TRANSACTION_getCellPhoneList, _data, _reply, 0);
                _reply.readException();
                _result = _reply.createTypedArrayList(com.rxt.bindersample.aidl.CellPhone.CREATOR);
            } finally {
                _reply.recycle();
                _data.recycle();
            }
            return _result;
		}

	> 该方法运行在客户端中。首先创建输入型parcel对象\_data，输出型parcel对象\_reply，以及返回结果\_result（如果有的话），然后把输入参数（如果有的话）写入\_data，再调用transact方法发起远程调用。此时客户端线程挂起直到\_result结果返回，所以，如果服务端方法为耗时操作，该方法不应放在UI线程中执行。

----------
<br>
<br>

### Binder工作机制流程图

![](https://i.imgur.com/uDYgdmZ.png)

#Binder FrameWork层源码分析

## binder framework流程图
![](https://i.imgur.com/dZXgLGw.png)

## 关键的类
- **ContextImpl** 

	ContextWrapper实现类，bindService方法

- **IActivityManager** 

	客户端与系统进程之间的aidl

- **ActivityManagerNative** 

	IActivityManager的Stub

- **ActivityManagerProxy** 

	IActivityManager的Proxy

- **ActivityManagerService** 

	ActivityManagerNative的实现类

- **ActiveServices**

- **ActivityThread**

## ContextImpl#bindServiceCommon

	int res = ActivityManagerNative.getDefault().bindService(
            mMainThread.getApplicationThread(), getActivityToken(), service,
            service.resolveTypeIfNeeded(getContentResolver()),
            sd, flags, getOpPackageName(), user.getIdentifier());    
由`ActivityManagerNative.getDefault()`可以发现IActivityManager，ActivityManagerNative，ActivityManagerProxy，ActivityManagerNative这几个客户端与系统进程间aidl相关的类

## AcitivityManagerService#bindService

	synchronized(this) {
			----------关键代码-----------
            return mServices.bindServiceLocked(caller, token, service,
                    resolvedType, connection, flags, callingPackage, userId);
        }
由这段代码进入ActiveServices中的bindServiceLocked方法

## ActiveServices#bindServiceLocked

- **bindServiceLocked** （关键方法）

	判断调用者是否为空（进程意外结束的情况）

		if (callerApp == null) {
	    throw new SecurityException(
	            "Unable to find app for caller " + caller
	            + " (pid=" + Binder.getCallingPid()
	            + ") when binding service " + service);
		}

	判断调用者是否为系统进程

		if (callerApp.info.uid == Process.SYSTEM_UID) {
            ......
        }

	保存Connection

		IBinder binder = connection.asBinder();
            ArrayList<ConnectionRecord> clist = s.connections.get(binder);
            if (clist == null) {
                clist = new ArrayList<ConnectionRecord>();
                s.connections.put(binder, clist);
            }
            clist.add(c);
            b.connections.add(c);

	如果flag是Context.BIND\_AUTO\_CREATE，调用bringUpServiceLocked方法唤起服务

		if ((flags&Context.BIND_AUTO_CREATE) != 0) {
	            s.lastActivity = SystemClock.uptimeMillis();
	            if (bringUpServiceLocked(s, service.getFlags(), callerFg, false,
	                    permissionsReviewRequired) != null) {
	                return 0;
	            }
	    }

	第一种情况：如果服务端进程已经起来了，服务也已经跑起来了，并且已经绑定过，第二种情况，进程、服务都已经起来了，但是没有绑定过

		if (s.app != null && b.intent.received) {
                // Service is already running, so we can immediately
                // publish the connection.
                try {
                    c.conn.connected(s.name, b.intent.binder);
                } catch (Exception e) {
                    Slog.w(TAG, "Failure sending service " + s.shortName
                            + " to connection " + c.conn.asBinder()
                            + " (in " + c.binding.client.processName + ")", e);
                }

                // If this is the first app connected back to this binding,
                // and the service had previously asked to be told when
                // rebound, then do so.
                if (b.intent.apps.size() == 1 && b.intent.doRebind) {
                    requestServiceBindingLocked(s, b.intent, callerFg, true);
                }
            } else if (!b.intent.requested) {
                requestServiceBindingLocked(s, b.intent, callerFg, false);
            }
	

## ActiveServices#bringupServiceLocked

即将唤起服务，把r从重启Service的list中移除，r的状态不再为重启状态

	// We are now bringing the service up, so no longer in the
        // restarting state.
        if (mRestartingServices.remove(r)) {
            r.resetRestartCounter();
            clearRestartingIfNeededLocked(r);
        }

服务的状态不再为延迟启动，我们现在就要启动服务了

	// Make sure this service is no longer considered delayed, we are starting it now.
        if (r.delayed) {
            if (DEBUG_DELAYED_STARTS) Slog.v(TAG_SERVICE, "REM FR DELAY LIST (bring up): " + r);
            getServiceMap(r.userId).mDelayedStartList.remove(r);
            r.delayed = false;
        }

确保服务所在的进程（即服务端进程）已经启动，如果没有，不让服务启动

	// Make sure that the user who owns this service is started.  If not,
        // we don't want to allow it to run.
        if (mAm.mStartedUsers.get(r.userId) == null) {
            String msg = "Unable to launch app "
                    + r.appInfo.packageName + "/"
                    + r.appInfo.uid + " for service "
                    + r.intent.getIntent() + ": user " + r.userId + " is stopped";
            Slog.w(TAG, msg);
            bringDownServiceLocked(r);
            return msg;
        }

服务已启动

	// Service is now being launched, its package can't be stopped.
        try {
            AppGlobals.getPackageManager().setPackageStoppedState(
                    r.packageName, false, r.userId);
        } catch (RemoteException e) {
        } catch (IllegalArgumentException e) {
            Slog.w(TAG, "Failed trying to unstop package "
                    + r.packageName + ": " + e);
        }

判断服务在服务端是否为单独的进程，如果不是，再进一步判断进程是否已经启动，如果已启动，则执行realStartServiceLocked。

	final boolean isolated = (r.serviceInfo.flags&ServiceInfo.FLAG_ISOLATED_PROCESS) != 0;
    final String procName = r.processName;
    ProcessRecord app;

	if (!isolated) {
        app = mAm.getProcessRecordLocked(procName, r.appInfo.uid, false);
        if (DEBUG_MU) Slog.v(TAG_MU, "bringUpServiceLocked: appInfo.uid=" + r.appInfo.uid
                    + " app=" + app);
        if (app != null && app.thread != null) {
            try {
                app.addPackage(r.appInfo.packageName, r.appInfo.versionCode, mAm.mProcessStats);
                realStartServiceLocked(r, app, execInFg);
                return null;
            } catch (TransactionTooLargeException e) {
                throw e;
            } catch (RemoteException e) {
                Slog.w(TAG, "Exception when starting service " + r.shortName, e);
            }

            // If a dead object exception was thrown -- fall through to
            // restart the application.
        }
    } else {
        // If this service runs in an isolated process, then each time
        // we call startProcessLocked() we will get a new isolated
        // process, starting another process if we are currently waiting
        // for a previous process to come up.  To deal with this, we store
        // in the service any current isolated process it is running in or
        // waiting to have come up.
        app = r.isolatedProc;
    }

如果服务端进程没有运行，则调用startProcessLocked启动进程，并在进程启动后启动服务

	// Not running -- get it started, and enqueue this service record
        // to be executed when the app comes up.
        if (app == null) {
            if ((app=mAm.startProcessLocked(procName, r.appInfo, true, intentFlags,
                    "service", r.name, false, isolated, false)) == null) {
                String msg = "Unable to launch app "
                        + r.appInfo.packageName + "/"
                        + r.appInfo.uid + " for service "
                        + r.intent.getIntent() + ": process is bad";
                Slog.w(TAG, msg);
                bringDownServiceLocked(r);
                return msg;
            }
            if (isolated) {
                r.isolatedProc = app;
            }
        }

## ActiveServices#realStartServiceLocked


	app.thread.scheduleCreateService(r, r.serviceInfo,
                    mAm.compatibilityInfoForPackageLocked(r.serviceInfo.applicationInfo),
                    app.repProcState);

	......
	requestServiceBindingsLocked(r, execInFg);

## ActivityThread#scheduleCreateService
调用创建服务方法

## ActivityThread#handleCreateService

创建服务

	LoadedApk packageInfo = getPackageInfoNoCheck(data.info.applicationInfo, data.compatInfo);
	    Service service = null;
	    try {
	        java.lang.ClassLoader cl = packageInfo.getClassLoader();
	        service = (Service) cl.loadClass(data.info.name).newInstance();
	    } catch (Exception e) {
	        if (!mInstrumentation.onException(service, e)) {
	            throw new RuntimeException(
	                "Unable to instantiate service " + data.info.name
	                + ": " + e.toString(), e);
	        }
    }


## ActiveServices#requestServiceBindingLocked
	
	if ((!i.requested || rebind) && i.apps.size() > 0) {
        try {
            bumpServiceExecutingLocked(r, execInFg, "bind");
            r.app.forceProcessStateUpTo(ActivityManager.PROCESS_STATE_SERVICE);
			------关键代码------
            r.app.thread.scheduleBindService(r, i.intent.getIntent(), rebind,
                    r.app.repProcState);
            if (!rebind) {
                i.requested = true;
            }
            i.hasBound = true;
            i.doRebind = false;
        } catch (TransactionTooLargeException e) {
            // Keep the executeNesting count accurate.
            if (DEBUG_SERVICE) Slog.v(TAG_SERVICE, "Crashed while binding " + r, e);
            final boolean inDestroying = mDestroyingServices.contains(r);
            serviceDoneExecutingLocked(r, inDestroying, inDestroying);
            throw e;
        } catch (RemoteException e) {
            if (DEBUG_SERVICE) Slog.v(TAG_SERVICE, "Crashed while binding " + r);
            // Keep the executeNesting count accurate.
            final boolean inDestroying = mDestroyingServices.contains(r);
            serviceDoneExecutingLocked(r, inDestroying, inDestroying);
            return false;
        }
    }

## ActivityThread#handleBindService

	private void handleBindService(BindServiceData data) {
        Service s = mServices.get(data.token);
        if (DEBUG_SERVICE)
            Slog.v(TAG, "handleBindService s=" + s + " rebind=" + data.rebind);
        if (s != null) {
            try {
                data.intent.setExtrasClassLoader(s.getClassLoader());
                data.intent.prepareToEnterProcess();
                try {
                    if (!data.rebind) {
                        IBinder binder = s.onBind(data.intent);
                        ActivityManagerNative.getDefault().publishService(
                                data.token, data.intent, binder);
                    } else {
                        s.onRebind(data.intent);
                        ActivityManagerNative.getDefault().serviceDoneExecuting(
                                data.token, SERVICE_DONE_EXECUTING_ANON, 0, 0);
                    }
                    ensureJitEnabled();
                } catch (RemoteException ex) {
                }
            } catch (Exception e) {
                if (!mInstrumentation.onException(s, e)) {
                    throw new RuntimeException(
                            "Unable to bind to service " + s
                            + " with " + data.intent + ": " + e.toString(), e);
                }
            }
        }
    }

## ActiveServices#publishServiceLocked

	void publishServiceLocked(ServiceRecord r, Intent intent, IBinder service) {
        final long origId = Binder.clearCallingIdentity();
        try {
            if (DEBUG_SERVICE) Slog.v(TAG_SERVICE, "PUBLISHING " + r
                    + " " + intent + ": " + service);
            if (r != null) {
                Intent.FilterComparison filter
                        = new Intent.FilterComparison(intent);
                IntentBindRecord b = r.bindings.get(filter);
			-------- 对b的一些属性进行赋值，这些将在ActiveServices#bindServiceLocked的判断中用到----------
                if (b != null && !b.received) {
                    b.binder = service;
                    b.requested = true;
                    b.received = true;
                    for (int conni=r.connections.size()-1; conni>=0; conni--) {
                        ArrayList<ConnectionRecord> clist = r.connections.valueAt(conni);
                        for (int i=0; i<clist.size(); i++) {
                            ConnectionRecord c = clist.get(i);
                            if (!filter.equals(c.binding.intent.intent)) {
                                if (DEBUG_SERVICE) Slog.v(
                                        TAG_SERVICE, "Not publishing to: " + c);
                                if (DEBUG_SERVICE) Slog.v(
                                        TAG_SERVICE, "Bound intent: " + c.binding.intent.intent);
                                if (DEBUG_SERVICE) Slog.v(
                                        TAG_SERVICE, "Published intent: " + intent);
                                continue;
                            }
                            if (DEBUG_SERVICE) Slog.v(TAG_SERVICE, "Publishing to: " + c);
                            try {
							----------把binder返回到客户端-----
                                c.conn.connected(r.name, service);
                            } catch (Exception e) {
                                Slog.w(TAG, "Failure sending service " + r.name +
                                      " to connection " + c.conn.asBinder() +
                                      " (in " + c.binding.client.processName + ")", e);
                            }
                        }
                    }
                }

                serviceDoneExecutingLocked(r, mDestroyingServices.contains(r), false);
            }
        } finally {
            Binder.restoreCallingIdentity(origId);
        }

## ActivityManagerService#startProcessLocked
> ActiveServices#bringupServiceLocked中调用，经过N次重载之后找到如下启动进程的代码

	// Start the process.  It will either succeed and return a result containing
    // the PID of the new process, or else throw a RuntimeException.
    boolean isActivityProcess = (entryPoint == null);
    if (entryPoint == null) entryPoint = "android.app.ActivityThread";
    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "Start proc: " +
            app.processName);
    checkTime(startTime, "startProcess: asking zygote to start proc");
    Process.ProcessStartResult startResult = Process.start(entryPoint,
            app.processName, uid, uid, gids, debugFlags, mountExternal,
            app.info.targetSdkVersion, app.info.seinfo, requiredAbi, 
			instructionSet,app.info.dataDir, entryPointArgs);

之后的过程回到ActiveServices#bindServiceLocked方法中

	if (s.app != null && b.intent.received) {
        // Service is already running, so we can immediately
        // publish the connection.
        try {
            c.conn.connected(s.name, b.intent.binder);
        } catch (Exception e) {
            Slog.w(TAG, "Failure sending service " + s.shortName
                    + " to connection " + c.conn.asBinder()
                    + " (in " + c.binding.client.processName + ")", e);
        }

        // If this is the first app connected back to this binding,
        // and the service had previously asked to be told when
        // rebound, then do so.
        if (b.intent.apps.size() == 1 && b.intent.doRebind) {
            requestServiceBindingLocked(s, b.intent, callerFg, true);
        }
    } else if (!b.intent.requested) {
		-----------在此处执行了绑定服务并把Binder返回给客户端的过程-----------
        requestServiceBindingLocked(s, b.intent, callerFg, false);
    }

#至此，Binder 源码解析结束！

