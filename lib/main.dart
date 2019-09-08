import 'dart:isolate';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:android_alarm_manager/android_alarm_manager.dart';

void main() async {
  final int helloAlarmID = 007;
  final int oneShotID = 1;

  await AndroidAlarmManager.initialize();
  runApp(MyApp());

  await AndroidAlarmManager.periodic(
    const Duration(seconds: 15),
    helloAlarmID,
    printMultiple,
    exact: true,
  );

  await AndroidAlarmManager.oneShot(
    const Duration(seconds: 5),
    oneShotID,
    printOnce,
  );
}

void printMessage(String msg) => print(' ${DateTime.now()} >>>>>> $msg');

void printOnce() => printMessage("Called only ONCE !!!!!");
void printMultiple() => printMessage("Called MULTIPLE TIMES !!!!!");

// void printHello() {
//   final DateTime now = DateTime.now();
//   final int isolateId = Isolate.current.hashCode;
//   print("[$now] Hello, world! isolate=$isolateId function='$printHello'");
// }

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: _Home(),
    );
  }
}

class _Home extends StatefulWidget {
  const _Home({
    Key key,
  }) : super(key: key);

  @override
  __HomeState createState() => __HomeState();
}

class __HomeState extends State<_Home> with WidgetsBindingObserver {
  static const platform = MethodChannel('com.example.services_demo/service');

  Future<void> connectToService() async {
    try {
      await platform.invokeMethod<void>('connect');
      print('Connected to service');
    } on Exception catch (e) {
      print(e.toString());
    }
  }

  Future<String> getDataFromService() async {
    try {
      final result = await platform.invokeMethod<String>('start');
      return result;
    } on PlatformException catch (e) {
      print(e.toString());
    }

    return 'No Data From Service';
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    connectToService();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);
    if (state == AppLifecycleState.paused ||
        state == AppLifecycleState.suspending) {
    } else if (state == AppLifecycleState.resumed) {
      connectToService();
    }
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Services Demo'),
      ),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Row(
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                Text('Data from Service: '),
                FutureBuilder<String>(
                  future: getDataFromService(),
                  builder: (context, snapshot) {
                    //
                    if (snapshot.hasData)
                      return Text(
                        snapshot.data,
                        style: TextStyle(
                          fontSize: 22.0,
                        ),
                      );

                    return Text('No Data');
                  },
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
