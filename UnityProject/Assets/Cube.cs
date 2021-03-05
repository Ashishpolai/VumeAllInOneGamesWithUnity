using System;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using UnityEngine.UI;
using UnityEngine;

#if UNITY_IOS || UNITY_TVOS
public class NativeAPI {
    [DllImport("__Internal")]
    public static extern void showHostMainWindow(string lastStringColor);
}
#endif

public class Cube : MonoBehaviour
{
    public Text text;    
    void appendToText(string line) { text.text += line + "\n"; }
    public GameObject btnHeading;

    void Update()
    {
        transform.Rotate(0, Time.deltaTime*10, 0);
		
		if (Application.platform == RuntimePlatform.Android)
            if (Input.GetKeyDown(KeyCode.Escape)) finishActivity();
    }

    void loadGame(String gameType)
    {
        switch (gameType)
        {
            case "ludo":
                GameObject cube = GameObject.FindGameObjectsWithTag("mycube")[0];
                    //Get the Renderer component from the new cube
                var cubeRenderer = cube.GetComponent<Renderer>();

                //Call SetColor using the shader property name "_Color" and setting the color to red
                cubeRenderer.material.SetColor("_Color", Color.green);
                showToast("Hello Ludo GREEN from Unity");
            break;

            case "poker":
                GameObject cubei = GameObject.FindGameObjectsWithTag("mycube")[0];
                //Get the Renderer component from the new cube
                var cubeRendereri = cubei.GetComponent<Renderer>();

                //Call SetColor using the shader property name "_Color" and setting the color to red
                cubeRendereri.material.SetColor("_Color", Color.yellow);
                showToast("Hello Poker YELLOW from Unity");
                break;
        }
    }

    string lastStringColor = "";
    void ChangeColor(string newColor)
    {
        appendToText( "Chancing Color to " + newColor );

        lastStringColor = newColor;
    
        if (newColor == "red") GetComponent<Renderer>().material.color = Color.red;
        else if (newColor == "blue") GetComponent<Renderer>().material.color = Color.blue;
        else if (newColor == "yellow") GetComponent<Renderer>().material.color = Color.yellow;
        else GetComponent<Renderer>().material.color = Color.black;
    }


    void showHostMainWindow()
    {
#if UNITY_ANDROID
        try
        {
            AndroidJavaClass jc = new AndroidJavaClass("com.company.product.OverrideUnityActivity");
            AndroidJavaObject overrideActivity = jc.GetStatic<AndroidJavaObject>("instance");
            overrideActivity.Call("showMainActivity", lastStringColor);
        } catch(Exception e)
        {
            appendToText("Exception during showHostMainWindow");
            appendToText(e.Message);
        }
#elif UNITY_IOS || UNITY_TVOS
        NativeAPI.showHostMainWindow(lastStringColor);
#endif
    }

    void showToast(String toastMsg)
    {
        #if UNITY_ANDROID
                try
                {
                    AndroidJavaClass jc = new AndroidJavaClass("com.company.product.OverrideUnityActivity");
                    AndroidJavaObject overrideActivity = jc.GetStatic<AndroidJavaObject>("instance");
                    overrideActivity.Call("showToast", toastMsg);
                }
                catch (Exception e)
                {
                    appendToText("Exception during showHostMainWindow");
                    appendToText(e.Message);
                }
        #endif
    }

    void finishActivity()
    {
        #if UNITY_ANDROID
                try
                {
                    AndroidJavaClass jc = new AndroidJavaClass("com.company.product.OverrideUnityActivity");
                    AndroidJavaObject overrideActivity = jc.GetStatic<AndroidJavaObject>("instance");
                    overrideActivity.Call("finishMyActivity");
                }
                catch (Exception e)
                {
                    appendToText("Exception during showHostMainWindow");
                    appendToText(e.Message);
                }
        #endif
    }

    void OnGUI()
    {
        GUIStyle style = new GUIStyle("button");
        style.fontSize = 30;        
        if (GUI.Button(new Rect(10, 10, 200, 100), "Red", style)) ChangeColor("red");
        if (GUI.Button(new Rect(10, 110, 200, 100), "Blue", style)) ChangeColor("blue");
        if (GUI.Button(new Rect(10, 300, 400, 100), "Show Main With Color", style)) showHostMainWindow();

       
        if (GUI.Button(new Rect(10, 400, 400, 100), "Show Toast", style)) showToast("Hello from unity!");
        if (GUI.Button(new Rect(440, 400, 400, 100), "Quit", style)) finishActivity();
    }
}

